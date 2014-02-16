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
package com.alex.bs.screens;

import com.alex.bs.BSGame;
import com.alex.bs.stages.BasicStage;
import com.alex.bs.stages.GameStage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract  class BasicScreen implements Screen {
    protected Stage stage;
    protected Camera camera;
    protected BSGame game;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public BasicScreen(BSGame game, Stage stage) {
        this.game = game;
        this.stage = stage;

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();

        if(stage instanceof BasicStage) {
            BasicStage basicStage = (BasicStage) stage;
            if(basicStage.isDebug()) {
                Matrix4 debugMatrix = new Matrix4(camera.combined);
                debugMatrix.scl(GameStage.BOX_TO_WORLD);

                Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
                debugRenderer.render(basicStage.getPhysicsWorld(), debugMatrix);

                shapeRenderer.setColor(Color.BLUE);
                shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                for(Actor a : stage.getRoot().getChildren()) {
                    shapeRenderer.identity();
                    //shapeRenderer.translate(-a.getWidth() / 2, -a.getHeight() / 2, 0);
                    shapeRenderer.translate(a.getX(), a.getY(), 0);
                    shapeRenderer.rotate(0, 0, 1, a.getRotation());
                    shapeRenderer.rect(-a.getWidth() / 2, -a.getHeight() / 2, a.getWidth(), a.getHeight());
                }
                shapeRenderer.end();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        /*camera.viewportWidth = 800;
        camera.viewportHeight = 480;
        camera.position.set(0, -10, 0f);*/
        //world.getSpriteBatch().setProjectionMatrix(camera.projection);
    }

    @Override
    public void show() {
        camera = (OrthographicCamera) stage.getCamera();
        camera.viewportHeight = 480;
        camera.viewportWidth = 800;

        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}