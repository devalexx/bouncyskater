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

import com.alex.bs.helper.Box2DSeparatorHelper;
import com.alex.bs.managers.ResourceManager;
import com.alex.bs.stages.GameStage;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

public class Mesh extends SimpleActor {
    private List<Vector2> vertices = new ArrayList<Vector2>();
    private static PolygonSpriteBatch polygonSpriteBatch = new PolygonSpriteBatch();
    private PolygonSprite poly;

    public Mesh(List<Vector2> vertices) {
        this.vertices.addAll(vertices);
        sprite = ResourceManager.getInstance().getSpriteFromDefaultAtlas("wall");
        type = TYPE.MESH;
        setSpriteAndBodyBox(20, 20);
    }

    public Mesh() {
        sprite = ResourceManager.getInstance().getSpriteFromDefaultAtlas("wall");
        type = TYPE.MESH;
        setSpriteAndBodyBox(20, 20);
    }

    @Override
    public void createPhysicsActor(World physicsWorld) {
        float top = vertices.get(0).y, left = vertices.get(0).x, right = vertices.get(0).x, bottom = vertices.get(0).y;
        for(Vector2 v : vertices) {
            if(v.x > right)
                right = v.x;
            if(v.x < left)
                left = v.x;
            if(v.y > top)
                top = v.y;
            if(v.y < bottom)
                bottom = v.y;
        }
        float width = Math.abs(right - left), height = Math.abs(top - bottom);
        Vector2 center = new Vector2(left + width / 2, bottom + height / 2);
        setSpriteAndBodyBox(width, height);
        if(pos.x == 0 && pos.y == 0) {
            setPosition(center);
            for(Vector2 v : vertices)
                v.sub(center);
        }

        List<Vector2> tmpVertices = new ArrayList<Vector2>();

        for(Vector2 v : vertices)
            tmpVertices.add(v.cpy().scl(GameStage.WORLD_TO_BOX));

        Box2DSeparatorHelper separatorHelper = new Box2DSeparatorHelper();
        if(separatorHelper.Validate(tmpVertices) != 0) {
            System.err.println("Can't separate vertices: " + separatorHelper.Validate(tmpVertices));
            return;
        }

        float verticesFloat[] = new float[2 * vertices.size()];
        for(int i = 0; i < vertices.size(); i++) {
            verticesFloat[i*2] = vertices.get(i).x + width / 2;
            verticesFloat[i*2+1] = vertices.get(i).y + height / 2;
        }

        Texture texture = ResourceManager.getInstance().getTexture("images/wall.png");
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        PolygonRegion polyReg = new PolygonRegion(new TextureRegion(texture, (int)width, (int)height), verticesFloat,
                new EarClippingTriangulator().computeTriangles(verticesFloat).toArray());

        poly = new PolygonSprite(polyReg);
        poly.setSize(width, height);
        poly.setOrigin(width / 2, height / 2);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        body = physicsWorld.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 10.4f;
        fixtureDef.filter.categoryBits = CATEGORY_SCENERY;
        fixtureDef.filter.maskBits = MASK_SCENERY;

        separatorHelper.Separate(body, fixtureDef, tmpVertices, 30);

        super.createPhysicsActor(physicsWorld);
    }

    public void addVertex(float x, float y) {
        vertices.add(new Vector2(x, y));
    }

    public List<Vector2> getVertices() {
        return vertices;
    }

    @Override
    public void draw(SpriteBatch batch, float parentAlpha) {
        batch.end();
        polygonSpriteBatch.setProjectionMatrix(batch.getProjectionMatrix());
        polygonSpriteBatch.begin();
        poly.setPosition(pos.x, pos.y);
        poly.setRotation(getRotation());
        poly.draw(polygonSpriteBatch);
        polygonSpriteBatch.end();
        batch.begin();
    }
}
