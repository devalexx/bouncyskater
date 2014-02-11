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

import com.alex.bs.stages.GameStage;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class SimpleActor extends Actor {
    protected Body body;
    protected TYPE type = TYPE.NONE;
    protected Vector2 pos = new Vector2();
    protected Vector2 offset = new Vector2();
    protected float rot;
    protected Vector2 linVel = new Vector2();
    protected Sprite sprite;

    public enum TYPE {
        NONE,
        GROUND,
        SKATE,
        PLAYER;
    }

    public void createPhysicsActor(World physicsWorld) {
        body.setTransform(pos.cpy().scl(GameStage.WORLD_TO_BOX), (float)Math.toRadians(rot));
    }

    public void prepareActor() {}

    public void setRotation(float a) {
        if(body != null)
            body.setTransform(getPosition(), (float)Math.toRadians(a));
        rot = a;
    }

    public float getRotation() {
        return rot;
    }

    public void setPosition(Vector2 vec) {
        if(body != null)
            body.setTransform(vec.cpy().scl(GameStage.WORLD_TO_BOX), body.getAngle());
        pos.set(vec);
    }

    public Vector2 getPosition() {
        return pos;
    }

    public void setLinearVelocity(Vector2 vec) {
        body.setLinearVelocity(vec.cpy().scl(GameStage.WORLD_TO_BOX));
        linVel.set(vec);
    }

    public Vector2 getLinearVelocity() {
        return linVel;
    }

    @Override
    public void act(float delta) {
        pos = body.getPosition();
        rot = (float)Math.toDegrees(body.getAngle());
        linVel = body.getLinearVelocity();
        pos.scl(GameStage.BOX_TO_WORLD);
        linVel.scl(GameStage.BOX_TO_WORLD);
    }

    public void applyForceToCenter(Vector2 vec) {
        body.applyForceToCenter(vec, true);
    }

    public void applyForceToCenter(float x, float y, boolean wake) {
        body.applyForceToCenter(x, y, wake);
    }

    public void applyLinearImpulse(Vector2 pos, Vector2 point) {
        body.applyLinearImpulse(pos, point, true);
    }

    public Body getBody() {
        return body;
    }

    public TYPE getType() {
        return type;
    }

    public String getStringType() {
        return type.toString();
    }

    public void setBodyBox(float width, float height) {
        setWidth(width);
        setHeight(height);
    }

    public void setSpriteBox(float width, float height) {
        sprite.setSize(width, height);
    }

    public float getPhysicsWidth() {
        return getWidth() * GameStage.WORLD_TO_BOX;
    }

    public float getPhysicsHeight() {
        return getHeight() * GameStage.WORLD_TO_BOX;
    }
}
