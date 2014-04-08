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

import com.alex.bs.models.SimpleActor;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class BasicStage extends Stage {
    public static final float WORLD_TO_BOX = 0.01f;
    public static final float BOX_TO_WORLD = 1 / WORLD_TO_BOX;
    protected World physicsWorld;
    protected boolean debug;
    protected Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    private HashMap<Body, SimpleActor> physicsActorMap = new HashMap<Body, SimpleActor>();
    private List<SimpleActor> actorsToRemove = new LinkedList<SimpleActor>();
    private List<SimpleActor> gameActors = new LinkedList<SimpleActor>();
    private Rectangle aabb = new Rectangle();

    protected BasicStage(float width, float height, boolean keepAspectRatio) {
        super(width, height, keepAspectRatio);
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);

        if(actor instanceof SimpleActor) {
            SimpleActor sa = ((SimpleActor) actor);
            gameActors.add(sa);
            sa.createPhysicsActor(physicsWorld);
            if(sa.getBody() != null)
                physicsActorMap.put(sa.getBody(), sa);
        }
    }

    public SimpleActor getActorByBody(Body body) {
        return physicsActorMap.get(body);
    }

    public void removeActor(Actor actor) {
        getRoot().removeActor(actor);

        if(actor instanceof SimpleActor) {
            gameActors.remove(actor);
            ((SimpleActor) actor).dispose();
        }
    }

    public void safeRemoveActor(SimpleActor sa) {
        actorsToRemove.add(sa);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    @Override
    public boolean keyDown(int keyCode) {
        switch(keyCode) {
            case Input.Keys.D:
                debug = !debug;
                break;
        }

        return super.keyDown(keyCode);
    }

    protected void drawDebug() {
        if(!debug)
            return;

        Matrix4 debugMatrix = new Matrix4(getCamera().combined);
        debugMatrix.scl(GameStage.BOX_TO_WORLD);
        debugRenderer.render(physicsWorld, debugMatrix);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for(SimpleActor sa : actorsToRemove)
            removeActor(sa);
    }

    public List<SimpleActor> getGameActors() {
        return gameActors;
    }

    public List<SimpleActor> getGameActorsByType(SimpleActor.TYPE type) {
        List<SimpleActor> actors = new LinkedList<SimpleActor>();

        for(SimpleActor sa : gameActors)
            if(sa.getType() == type)
                actors.add(sa);

        return actors;
    }

    public boolean isInStageRect(Actor actor) {
        return aabb.contains(actor.getX(), actor.getY());
    }

    public void setAABB(float x1, float y1, float x2, float y2) {
        aabb.set(x1, y1, x2-x1, y2-y1);
    }
}
