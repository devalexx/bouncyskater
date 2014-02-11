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
package com.alex.bs.stages;

import com.alex.bs.models.Ground;
import com.alex.bs.models.Player;
import com.alex.bs.models.SimpleActor;
import com.alex.bs.models.Skate;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class GameStage extends Stage {
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;
    private World physicsWorld;
    private Skate skate;
    private Player player;
    private Action leftAction, rightAction;
    private Joint joint;
    private float MAX_VELOCITY = 100;
    private boolean canJump;

    public GameStage(float width, float height) {
        super(width, height, true);

        physicsWorld = new World(new Vector2(0, -9.8f), true);

        skate = new Skate();
        addActor(skate);

        Ground ground = new Ground();
        ground.setPosition(new Vector2(0, -50));
        addActor(ground);
        ground = new Ground();
        ground.setPosition(new Vector2(-200, -50));
        ground.setRotation(-10);
        addActor(ground);
        ground = new Ground();
        ground.setPosition(new Vector2(200, -50));
        ground.setRotation(90);
        addActor(ground);

        player = new Player();
        player.setPosition(new Vector2(100, 50));
        addActor(player);

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.initialize(player.getBody(), skate.getBody(), player.getBody().getWorldCenter());
        //joint = physicsWorld.createJoint(jointDef);
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);

        if(actor instanceof SimpleActor)
            ((SimpleActor) actor).createPhysicsActor(physicsWorld);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        physicsWorld.step(1 / 60f, 8, 3);

        if(joint != null &&
                (joint.getReactionForce(1 / 60f).len() > 0.003 ||
                        skate.getRotation() < -60 ||
                        skate.getRotation() > 60)) {
            physicsWorld.destroyJoint(joint);
            joint = null;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(joint != null) {
                if(skate.getLinearVelocity().x > -MAX_VELOCITY * 3)
                    skate.applyForceToCenter(-2, 0, true);
            } else if(player.getLinearVelocity().x > -MAX_VELOCITY)
                player.applyForceToCenter(-2, 0, true);
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(joint != null) {
                if(skate.getLinearVelocity().x < MAX_VELOCITY * 3)
                    skate.applyForceToCenter(1, 0, true);
            } else if(player.getLinearVelocity().x < MAX_VELOCITY)
                player.applyForceToCenter(2, 0, true);
            float a = player.getBody().getFixtureList().get(0).getFriction();
        }

        canJump = isPlayerGrounded();
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch(keyCode) {
            case Input.Keys.SPACE:
                if(joint == null) {
                    player.setPosition(skate.getPosition().cpy().add(0, player.getHeight() / 1.3f));
                    RevoluteJointDef jointDef = new RevoluteJointDef();
                    jointDef.initialize(player.getBody(), skate.getBody(), player.getBody().getWorldCenter());
                    joint = physicsWorld.createJoint(jointDef);
                } else {
                    physicsWorld.destroyJoint(joint);
                    joint = null;
                }
                break;
            case Input.Keys.UP:
                if(canJump)
                    player.applyForceToCenter(new Vector2(0, 30));
                break;
        }

        return super.keyDown(keyCode);
    }

    private boolean isPlayerGrounded() {
        Array<Contact> contactList = physicsWorld.getContactList();
        for(int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);

            if(contact.isTouching() && (contact.getFixtureA() == player.getPlayerSensorFixture() ||
                    contact.getFixtureB() == player.getPlayerSensorFixture())) {
                Vector2 pos = player.getPosition();
                WorldManifold manifold = contact.getWorldManifold();
                boolean below = true;
                for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
                    below &= (manifold.getPoints()[j].y < pos.y - 0.4f);
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
}
