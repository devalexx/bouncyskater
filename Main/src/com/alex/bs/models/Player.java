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
import com.alex.bs.stages.GameStage;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;
import com.badlogic.gdx.utils.Array;

import java.util.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class Player extends SimpleActor {
    private Fixture playerPhysicsFixture, playerSensorFixture;
    private boolean canStandUp = true, standUp = true, dead = false, playerGrounded;
    private Joint skateJoint;
    private Skate skate;
    private float MAX_VELOCITY = 125;
    private HashMap<String, Body> ragdollBodiesMap = new HashMap<String, Body>();
    private HashMap<String, Vector2> ragdollBodiesSize = new HashMap<String, Vector2>();
    private HashMap<String, Vector2> ragdollBodiesOffset = new HashMap<String, Vector2>();
    private Animation standAnimation, runAnimation;
    private TextureRegion[] standFrames = new TextureRegion[FRAME_COLS];
    private TextureRegion[] runFrames = new TextureRegion[FRAME_COLS];
    private float stateTime = 0;
    private boolean needRemoveBody = false, needRemoveRagdollBody = false;
    private Sprite spriteBlack, spriteBlackCircle;
    private Vector2 tmpVel = new Vector2();

    private static final int FRAME_COLS = 3;
    private static final int FRAME_ROWS = 2;

    public Player() {
        sprite = ResourceManager.getInstance().getSpriteFromDefaultAtlas("player");
        spriteBlack = ResourceManager.getInstance().getSpriteFromDefaultAtlas("black");
        spriteBlackCircle = ResourceManager.getInstance().getSpriteFromDefaultAtlas("black_circle");
        TextureRegion t = ResourceManager.getInstance().getRegionFromDefaultAtlas("player_sheet");
        TextureRegion[][] tmp = t.split(t.getRegionWidth() / FRAME_COLS, t.getRegionHeight() / FRAME_ROWS);

        System.arraycopy(tmp[0], 0, standFrames, 0, FRAME_COLS);
        System.arraycopy(tmp[1], 0, runFrames, 0, FRAME_COLS);

        runAnimation = new Animation(0.15f, runFrames);
        standAnimation = new Animation(0.15f, standFrames);

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

    public void createRagdollPhysicsActor() {
        Filter filter = new Filter();
        filter.maskBits = MASK_PLAYER;
        filter.categoryBits = CATEGORY_PLAYER;

        BodyDef def = new BodyDef();
        def.type = bodyType;

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(getPhysicsWidth() / 10, getPhysicsHeight() / 2 * 0.4f);
        Body torsoBody = physicsWorld.createBody(def);
        torsoBody.createFixture(poly, 1).setFilterData(filter);

        ragdollBodiesSize.put("torso", new Vector2(getPhysicsWidth() / 10, getPhysicsHeight() / 2 * 0.4f).scl(GameStage.BOX_TO_WORLD));
        ragdollBodiesOffset.put("torso", new Vector2(0, 0));
        ragdollBodiesMap.put("torso", torsoBody);

        CircleShape circle = new CircleShape();
        circle.setRadius(getPhysicsWidth() / 3);
        circle.setPosition(new Vector2(0, getPhysicsHeight() / 2 * 0.65f));
        Body headBody = physicsWorld.createBody(def);
        headBody.createFixture(circle, 1).setFilterData(filter);

        ragdollBodiesSize.put("head", new Vector2(getPhysicsWidth() / 3, getPhysicsWidth() / 3).scl(GameStage.BOX_TO_WORLD));
        ragdollBodiesOffset.put("head", new Vector2(0, getPhysicsHeight() / 2 * 0.65f));
        ragdollBodiesMap.put("head", headBody);

        poly = new PolygonShape();
        poly.setAsBox(getPhysicsWidth() / 14, getPhysicsHeight() / 2 * 0.3f,
                new Vector2(0, getPhysicsHeight() * 0.05f), 0);
        Body leftHandBody = physicsWorld.createBody(def);
        leftHandBody.createFixture(poly, 1).setFilterData(filter);

        ragdollBodiesSize.put("leftHand", new Vector2(getPhysicsWidth() / 14, getPhysicsHeight() / 2 * 0.3f).scl(GameStage.BOX_TO_WORLD));
        ragdollBodiesOffset.put("leftHand", new Vector2(0, getPhysicsHeight() * 0.05f));
        ragdollBodiesMap.put("leftHand", leftHandBody);

        poly = new PolygonShape();
        poly.setAsBox(getPhysicsWidth() / 14, getPhysicsHeight() / 2 * 0.3f,
                new Vector2(0, getPhysicsHeight() * 0.05f), 0);
        Body rightHandBody = physicsWorld.createBody(def);
        rightHandBody.createFixture(poly, 1).setFilterData(filter);

        ragdollBodiesSize.put("rightHand", new Vector2(getPhysicsWidth() / 14, getPhysicsHeight() / 2 * 0.3f).scl(GameStage.BOX_TO_WORLD));
        ragdollBodiesOffset.put("rightHand", new Vector2(0, getPhysicsHeight() * 0.05f));
        ragdollBodiesMap.put("rightHand", rightHandBody);

        poly = new PolygonShape();
        poly.setAsBox(getPhysicsWidth() / 14, getPhysicsHeight() / 2 * 0.4f,
                new Vector2(0, -getPhysicsHeight() * 0.4f), 0);
        Body leftLegBody = physicsWorld.createBody(def);
        leftLegBody.createFixture(poly, 1).setFilterData(filter);

        ragdollBodiesSize.put("leftLeg", new Vector2(getPhysicsWidth() / 14, getPhysicsHeight() / 2 * 0.4f).scl(GameStage.BOX_TO_WORLD));
        ragdollBodiesOffset.put("leftLeg", new Vector2(0, -getPhysicsHeight() * 0.4f));
        ragdollBodiesMap.put("leftLeg", leftLegBody);

        poly = new PolygonShape();
        poly.setAsBox(getPhysicsWidth() / 14, getPhysicsHeight() / 2 * 0.4f,
                new Vector2(0, -getPhysicsHeight() * 0.4f), 0);
        Body rightLegBody = physicsWorld.createBody(def);
        rightLegBody.createFixture(poly, 1).setFilterData(filter);

        ragdollBodiesSize.put("rightLeg", new Vector2(getPhysicsWidth() / 14, getPhysicsHeight() / 2 * 0.4f).scl(GameStage.BOX_TO_WORLD));
        ragdollBodiesOffset.put("rightLeg", new Vector2(0, -getPhysicsHeight() * 0.4f));
        ragdollBodiesMap.put("rightLeg", rightLegBody);

        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.enableLimit = true;

        revoluteJointDef.initialize(torsoBody, headBody, new Vector2(0, getPhysicsHeight() / 2 * 0.55f));
        revoluteJointDef.lowerAngle = MathUtils.degreesToRadians * -45;
        revoluteJointDef.upperAngle = MathUtils.degreesToRadians * 45;
        physicsWorld.createJoint(revoluteJointDef);

        revoluteJointDef.enableLimit = false;

        revoluteJointDef.initialize(leftHandBody, torsoBody, new Vector2(0, getPhysicsHeight() / 2 * 0.4f));
        physicsWorld.createJoint(revoluteJointDef);

        revoluteJointDef.initialize(rightHandBody, torsoBody, new Vector2(0, getPhysicsHeight() / 2 * 0.4f));
        physicsWorld.createJoint(revoluteJointDef);

        revoluteJointDef.initialize(leftLegBody, torsoBody, new Vector2(0, -getPhysicsHeight() / 2 * 0.4f));
        physicsWorld.createJoint(revoluteJointDef);

        revoluteJointDef.initialize(rightLegBody, torsoBody, new Vector2(0, -getPhysicsHeight() / 2 * 0.4f));
        physicsWorld.createJoint(revoluteJointDef);

        for(Body b : ragdollBodiesMap.values())
            b.setTransform(pos.cpy().add(getWidth() / 2, getHeight() / 2)
                    .scl(GameStage.WORLD_TO_BOX), (float)Math.toRadians(getRotation()));
    }

    @Override
    public void setPosition(float x, float y, boolean applyToBody) {
        super.setPosition(x, y, applyToBody);

        if(applyToBody && ragdollBodiesMap.size() > 0)
            for(Body b : ragdollBodiesMap.values()) {
                b.setTransform(pos.cpy().scl(GameStage.WORLD_TO_BOX), (float)Math.toRadians(getRotation()));
            }
    }

    public void roll(float force) {
        body.applyTorque(force, true);
    }

    public Fixture getPlayerSensorFixture() {
        return playerSensorFixture;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        if(ragdollBodiesMap.size() > 0) {
            for(String name : ragdollBodiesMap.keySet()) {
                Body b = ragdollBodiesMap.get(name);
                Vector2 pos = b.getPosition().cpy().scl(GameStage.BOX_TO_WORLD);
                Vector2 size = ragdollBodiesSize.get(name);
                Vector2 offset = ragdollBodiesOffset.get(name).cpy().scl(GameStage.BOX_TO_WORLD);

                Sprite sprite = spriteBlack;
                if(name.equals("head"))
                    sprite = spriteBlackCircle;

                sprite.setPosition(pos.x - size.x + offset.x, pos.y - size.y + offset.y);
                sprite.setRotation((float) Math.toDegrees(b.getAngle()));
                sprite.setSize(size.x * 2, size.y * 2);
                sprite.setOrigin(size.x - offset.x, size.y - offset.y);
                sprite.draw(batch);
            }
        } else {
            stateTime += Gdx.graphics.getDeltaTime();
            TextureRegion currentFrame = null;
            if(!standUp) {
                sprite.setPosition(getX(), getY());
                sprite.setRotation(getRotation());
                sprite.draw(batch);
            } else if(skate == null &&
                    (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))) {
                currentFrame = runAnimation.getKeyFrame(stateTime, true);
                if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !currentFrame.isFlipX())
                    currentFrame.flip(true, false);
                else if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && currentFrame.isFlipX())
                    currentFrame.flip(true, false);
            } else {
                currentFrame = standAnimation.getKeyFrame(stateTime, true);
            }

            if(currentFrame != null) {
                batch.draw(currentFrame, getX(), getY(), getOriginX(), getOriginY(),
                        getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
            }
        }
    }

    public void fall() {
        if(!standUp)
            return;

        canStandUp = false;
        standUp = false;
        needRemoveBody = true;

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
            needRemoveRagdollBody = true;
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
        if(needRemoveBody) {
            tmpVel.set(linVel);
            physicsWorld.destroyBody(body);
            body = null;
            needRemoveBody = false;

            createRagdollPhysicsActor();

            for(Body b : ragdollBodiesMap.values())
                b.setLinearVelocity(tmpVel.scl(GameStage.WORLD_TO_BOX).scl(2));
        }
        if(needRemoveRagdollBody) {
            for(Body b : ragdollBodiesMap.values())
                physicsWorld.destroyBody(b);
            ragdollBodiesMap.clear();
            needRemoveRagdollBody = false;

            createPhysicsActor(physicsWorld);
            setPosition(getX(), getY() + (getHeight() / 2) - (float) Math.cos(Math.toRadians(getRotation())) * getHeight() / 2);
            if(getRotation() != 0)
                setRotation(0);
        }

        super.act(delta);

        if(ragdollBodiesMap.size() > 0) {
            Body torsoBody = ragdollBodiesMap.get("head");
            Vector2 pos = torsoBody.getPosition();
            setRotation((float)Math.toDegrees(torsoBody.getAngle()), false);
            linVel = torsoBody.getLinearVelocity();
            pos.scl(GameStage.BOX_TO_WORLD).sub(physOffset);
            linVel.scl(GameStage.BOX_TO_WORLD);
            setPosition(pos.x, pos.y, false);
        }

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
                    applyForceToCenter(-3, 0, true);
            }
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(standUp()) {
                if(skateJoint != null) {
                    if(skate.getLinearVelocity().x < MAX_VELOCITY * 3)
                        skate.applyForceToCenter(2, 0, true);
                } else if(getLinearVelocity().x < MAX_VELOCITY)
                    applyForceToCenter(3, 0, true);
            }
        }
    }

    public void kill() {
        dead = true;
        fall();
    }
}
