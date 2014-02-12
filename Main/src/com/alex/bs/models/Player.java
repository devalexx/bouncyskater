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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Player extends SimpleActor {
    private Fixture playerPhysicsFixture, playerSensorFixture;

    public Player() {
        //sprite = TextureManager.getInstance().getSpriteFromDefaultAtlas("cloud");
        type = TYPE.PLAYER;
        setBodyBox(20, 80);
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
}
