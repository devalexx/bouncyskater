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

import com.alex.bs.models.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class GameStage extends Stage {
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;
    private World physicsWorld;
    private Skate skate;
    private Player player;
    private Action leftAction, rightAction;
    private Joint joint;

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
        player.setPosition(new Vector2(0, 50));
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

        if(joint != null && joint.getReactionForce(1 / 60f).len() > 0.004) {
            physicsWorld.destroyJoint(joint);
            joint = null;
        }
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch(keyCode) {
            case Input.Keys.LEFT:
                leftAction = forever(
                    run(new Runnable() {
                        @Override
                        public void run() {
                            if(joint != null)
                                skate.roll(0.04f);
                            else
                                player.roll(0.2f);
                        }
                    })
                );
                addAction(leftAction);
                break;
            case Input.Keys.RIGHT:
                rightAction = forever(
                    run(new Runnable() {
                        @Override
                        public void run() {
                            if(joint != null)
                                skate.roll(-0.04f);
                            else
                                player.roll(-0.2f);
                        }
                    })
                );
                addAction(rightAction);
                break;
            case Input.Keys.SPACE:
                if(joint == null) {
                    player.setPosition(skate.getPosition().cpy().add(0, 50));
                    RevoluteJointDef jointDef = new RevoluteJointDef();
                    jointDef.initialize(player.getBody(), skate.getBody(), player.getBody().getWorldCenter());
                    joint = physicsWorld.createJoint(jointDef);
                }
                break;
        }

        return super.keyDown(keyCode);
    }

    @Override
    public boolean keyUp(int keyCode) {
        switch(keyCode) {
            case Input.Keys.LEFT:
                getRoot().removeAction(leftAction);
                leftAction = null;
                break;
            case Input.Keys.RIGHT:
                getRoot().removeAction(rightAction);
                rightAction = null;
                break;
        }

        return super.keyUp(keyCode);
    }
}
