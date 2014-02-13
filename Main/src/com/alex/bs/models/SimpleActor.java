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
    protected Vector2 linVel = new Vector2();
    protected Sprite sprite;

    final short CATEGORY_PLAYER = 0x0001;
    final short CATEGORY_SKATE = 0x0002;
    final short CATEGORY_SCENERY = 0x0004;

    final short MASK_PLAYER = CATEGORY_SCENERY;
    final short MASK_SKATE = CATEGORY_SCENERY;
    final short MASK_SCENERY = -1;

    public enum TYPE {
        NONE,
        WALL,
        SKATE,
        PLAYER
    }

    public void createPhysicsActor(World physicsWorld) {
        body.setTransform(new Vector2(getX(), getY()).scl(GameStage.WORLD_TO_BOX), (float)Math.toRadians(getRotation()));
    }

    public void prepareActor() {}

    public void setRotation(float a, boolean applyToBody) {
        if(body != null && applyToBody)
            body.setTransform(new Vector2(getX(), getY()).scl(GameStage.WORLD_TO_BOX), (float)Math.toRadians(a));
        super.setRotation(a);
    }

    @Override
    public void setRotation(float degrees) {
        setRotation(degrees, true);
    }

    public void setPosition(Vector2 vec) {
        setPosition(vec, true);
    }

    public void setPosition(Vector2 vec, boolean applyToBody) {
        setPosition(vec.x, vec.y, applyToBody);
    }

    @Override
    public void setPosition(float x, float y) {
        setPosition(x, y, true);
    }

    public void setPosition(float x, float y, boolean applyToBody) {
        if(body != null && applyToBody)
            body.setTransform(new Vector2(x, y).scl(GameStage.WORLD_TO_BOX), body.getAngle());
        super.setPosition(x, y);
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
        super.act(delta);
        Vector2 pos = body.getPosition();
        setRotation((float)Math.toDegrees(body.getAngle()), false);
        linVel = body.getLinearVelocity();
        pos.scl(GameStage.BOX_TO_WORLD);
        linVel.scl(GameStage.BOX_TO_WORLD);
        setPosition(pos.x, pos.y, false);
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
        sprite.setOrigin(width / 2, height / 2);
    }

    public float getPhysicsWidth() {
        return getWidth() * GameStage.WORLD_TO_BOX;
    }

    public float getPhysicsHeight() {
        return getHeight() * GameStage.WORLD_TO_BOX;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        if(sprite == null)
            return;

        sprite.setPosition(getX() - sprite.getWidth() / 2, getY() - sprite.getHeight() / 2);
        sprite.setRotation(getRotation());
        sprite.draw(batch);
    }

    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }
}
