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
    protected World physicsWorld;
    protected Vector2 pos = new Vector2();
    protected Vector2 physOffset = new Vector2();
    protected BodyDef.BodyType bodyType = BodyDef.BodyType.StaticBody;

    final short CATEGORY_PLAYER = 0x0001;
    final short CATEGORY_SKATE = 0x0002;
    final short CATEGORY_SCENERY = 0x0004;
    final short CATEGORY_PICKED = 0x0008;

    final short MASK_PLAYER = CATEGORY_SCENERY | CATEGORY_PICKED;
    final short MASK_SKATE = CATEGORY_SCENERY;
    final short MASK_SCENERY = -1;
    final short MASK_PICKED = CATEGORY_PLAYER;

    public enum TYPE {
        NONE,
        WALL,
        SKATE,
        PLAYER,
        MESH,
        COIN
    }

    public void createPhysicsActor(World physicsWorld) {
        this.physicsWorld = physicsWorld;
        body.setTransform(pos.cpy().scl(GameStage.WORLD_TO_BOX), (float)Math.toRadians(getRotation()));
    }

    public void setRotation(float a, boolean applyToBody) {
        if(body != null && applyToBody)
            body.setTransform(body.getPosition(), (float)Math.toRadians(a));
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
            body.setTransform(new Vector2(x, y).add(physOffset).scl(GameStage.WORLD_TO_BOX), body.getAngle());
        pos.set(x, y);
        super.setPosition(x, y);
    }

    @Override
    public void translate(float x, float y) {
        setPosition(pos.cpy().add(x, y));
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
        if(body != null) {
            Vector2 pos = body.getPosition();
            setRotation((float)Math.toDegrees(body.getAngle()), false);
            linVel = body.getLinearVelocity();
            pos.scl(GameStage.BOX_TO_WORLD).sub(physOffset);
            linVel.scl(GameStage.BOX_TO_WORLD);
            setPosition(pos.x, pos.y, false);
        }
    }

    public void applyForceToCenter(Vector2 vec) {
        if(body != null)
            body.applyForceToCenter(vec, true);
    }

    public void applyForceToCenter(float x, float y, boolean wake) {
        if(body != null)
            body.applyForceToCenter(x, y, wake);
    }

    public void applyLinearImpulse(Vector2 pos, Vector2 point) {
        if(body != null)
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

    public void setSpriteAndBodyBox(float width, float height) {
        setBodyBox(width, height);
        setSpriteBox(width, height);
    }

    public void setBodyBox(float width, float height) {
        super.setSize(width, height);
        setOrigin(width / 2, height / 2);
        physOffset.set(width / 2, height / 2);
    }

    public void setSpriteBox(float width, float height) {
        sprite.setSize(width, height);
        sprite.setOrigin(width / 2, height / 2);
    }

    @Override
    public void setSize(float width, float height) {
        setBodyBox(width, height);
        setSpriteBox(width, height);

        if(body != null) {
            physicsWorld.destroyBody(body);
            createPhysicsActor(physicsWorld);
        }
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

        sprite.setPosition(getX() , getY());
        sprite.setRotation(getRotation());
        sprite.draw(batch);
    }

    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    public void dispose() {
        if(body != null)
            physicsWorld.destroyBody(body);
        body = null;
    }

    public BodyDef.BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyDef.BodyType bodyType) {
        this.bodyType = bodyType;
    }
}
