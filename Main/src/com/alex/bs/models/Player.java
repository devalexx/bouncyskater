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

import com.alex.bs.managers.TextureManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class Player extends SimpleActor {
    private Fixture playerPhysicsFixture, playerSensorFixture;
    private boolean canStandUp = true, standUp = true;

    public Player() {
        sprite = TextureManager.getInstance().getSpriteFromDefaultAtlas("player");
        type = TYPE.PLAYER;
        setBodyBox(20, 80);
        setSpriteBox(30, 90);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        Filter filter = new Filter();
        filter.maskBits = MASK_PLAYER;
        filter.categoryBits = CATEGORY_PLAYER;

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        body = physicsWorld.createBody(def);

        PolygonShape poly = new PolygonShape();
        poly.setAsBox(getPhysicsWidth() / 2, getPhysicsHeight() / 2);
        playerPhysicsFixture = body.createFixture(poly, 1);
        playerPhysicsFixture.setFilterData(filter);
        poly.dispose();

        CircleShape circle = new CircleShape();
        circle.setRadius(getPhysicsWidth() / 2);
        circle.setPosition(new Vector2(0, -getPhysicsHeight() / 2));
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
        sprite.setPosition(getX() - getWidth() / 1.3f, getY() - getHeight() / 1.6f);
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

        if(canStandUp) {
            if(getRotation() != 0)
                setRotation(0);
            body.setFixedRotation(true);
            standUp = true;
            return true;
        } else
            return false;
    }
}
