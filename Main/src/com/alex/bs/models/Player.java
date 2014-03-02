/*******************************************************************************
 * Copyright 2013 See AUTHORS file.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE V3
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.alex.bs.models;

import com.alex.bs.managers.ResourceManager;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.utils.Array;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class Player extends SimpleActor {
    private Fixture playerPhysicsFixture, playerSensorFixture;
    private boolean canStandUp = true, standUp = true, dead = false, playerGrounded;
    private Joint skateJoint;
    private Skate skate;
    private float MAX_VELOCITY = 100;

    public Player() {
        sprite = ResourceManager.getInstance().getSpriteFromDefaultAtlas("player");
        type = TYPE.PLAYER;
        setBodyBox(30, 90);
        setSpriteBox(30, 90);
        physOffset.set(15, 50);
        bodyType = BodyDef.BodyType.DynamicBody;
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        Filter filter = new Filter();
        filter.maskBits = MASK_PLAYER;
        filter.categoryBits = CATEGORY_PLAYER;

        BodyDef def = new BodyDef();
        def.type = bodyType;
        body = physicsWorld.createBody(def);

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(getPhysicsWidth() / 2 * 0.66f, getPhysicsHeight() / 2 * 0.9f);
        playerPhysicsFixture = body.createFixture(poly, 1);
        playerPhysicsFixture.setFilterData(filter);
        poly.dispose();

        CircleShape circle = new CircleShape();
        circle.setRadius(getPhysicsWidth() / 2 * 0.66f);
        circle.setPosition(new Vector2(0, -getPhysicsHeight() / 2 * 0.9f));
        playerSensorFixture = body.createFixture(circle, 0);
        playerSensorFixture.setFriction(100);
        playerSensorFixture.setFilterData(filter);
        circle.dispose();

        body.setBullet(true);
        body.setFixedRotation(true);

        super.createPhysicsActor(physicsWorld);
    }

    public void roll(float force) {
        body.applyTorque(force, true);
    }

    public Fixture getPlayerSensorFixture() {
        return playerSensorFixture;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        sprite.setPosition(getX(), getY());
        sprite.setRotation(getRotation());
        sprite.draw(batch);
    }

    public void fall() {
        if(!standUp)
            return;

        canStandUp = false;
        standUp = false;
        body.setFixedRotation(false);
        addAction(
            sequence(
                delay(1),
                run(new Runnable() {
                    @Override
                    public void run() {
                        canStandUp = true;
                    }
                })
            )
        );
    }

    public boolean standUp() {
        if(standUp)
            return true;

        if(dead)
            return false;

        if(canStandUp) {
            setPosition(getX(), getY() + (getHeight() / 2) - (float) Math.cos(Math.toRadians(getRotation())) * getHeight() / 2);
            if(getRotation() != 0)
                setRotation(0);
            body.setFixedRotation(true);
            standUp = true;
            return true;
        } else
            return false;
    }

    // Hack fix friction
    private boolean checkPlayerGroundedAndHack() {
        Array<Contact> contactList = physicsWorld.getContactList();
        for(int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);

            if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
                    contact.getFixtureB() == playerSensorFixture)) {
                WorldManifold manifold = contact.getWorldManifold();
                boolean below = true;
                for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
                    //below &= (manifold.getPoints()[j].y < getY() - 0.4f);
                }

                if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && below)
                    contact.setFriction(100F);
                else
                    contact.setFriction(0F);

                return below;
            }
        }

        return false;
    }

    public boolean isPlayerGrounded() {
        return playerGrounded;
    }

    public void attachSkate(Skate skate) {
        if(standUp() && skateJoint == null && getPosition().dst(skate.getPosition()) < 50) {
            setPosition(new Vector2(skate.getX() + skate.getWidth() / 2, skate.getY() + skate.getHeight() / 2)
                    .sub(getWidth() / 2, 0));
            RevoluteJointDef jointDef = new RevoluteJointDef();
            jointDef.initialize(getBody(), skate.getBody(), getBody().getWorldCenter());
            skateJoint = physicsWorld.createJoint(jointDef);
            ((RevoluteJoint)skateJoint).enableLimit(true);
            ((RevoluteJoint)skateJoint).setLimits(0, 0);
            this.skate = skate;
            getBody().setFixedRotation(false);
        }
    }

    public void detachSkate() {
        if(skateJoint != null) {
            physicsWorld.destroyJoint(skateJoint);
            skateJoint = null;
            this.skate = null;
            if(standUp) {
                getBody().setFixedRotation(true);
                getBody().setTransform(getBody().getPosition(), 0);
            }
        }
    }

    public boolean isSkateAttached() {
        return skateJoint != null;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        playerGrounded = checkPlayerGroundedAndHack();

        if(skateJoint != null &&
                (skateJoint.getReactionForce(1 / 60f).len() > 0.005 ||
                        skate.getRotation() < -60 ||
                        skate.getRotation() > 60)) {
            fall();
            detachSkate();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(standUp()) {
                if(skateJoint != null) {
                    if(skate.getLinearVelocity().x > -MAX_VELOCITY * 3)
                        skate.applyForceToCenter(-2, 0, true);
                } else if(getLinearVelocity().x > -MAX_VELOCITY)
                    applyForceToCenter(-2, 0, true);
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(standUp()) {
                if(skateJoint != null) {
                    if(skate.getLinearVelocity().x < MAX_VELOCITY * 3)
                        skate.applyForceToCenter(1, 0, true);
                } else if(getLinearVelocity().x < MAX_VELOCITY)
                    applyForceToCenter(2, 0, true);
            }
        }
    }

    public void kill() {
        dead = true;
        fall();
    }
}
