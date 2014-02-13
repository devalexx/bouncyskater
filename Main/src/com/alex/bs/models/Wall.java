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
import com.alex.bs.stages.GameStage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Wall extends SimpleActor {
    public Wall() {
        sprite = TextureManager.getInstance().getSpriteFromDefaultAtlas("wall");
        type = TYPE.WALL;
        setSpriteAndBodyBox(400, 20);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(getPhysicsWidth() / 2, getPhysicsHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.density = 1;
        fixtureDef.filter.categoryBits = CATEGORY_SCENERY;
        fixtureDef.filter.maskBits = MASK_SCENERY;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = physicsWorld.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.resetMassData();

        polygonShape.dispose();

        super.createPhysicsActor(physicsWorld);
    }
}
