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
package com.alex.bs.managers;

import com.alex.bs.models.*;
import com.alex.bs.ui.EditorUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;

public class EditorManager {
    private Stage stage;
    private int counter;
    private SimpleActor selectedActor;
    private EditorUI editorUI;

    public EditorManager(Stage stage) {
        this.stage = stage;
    }

    public void setEditorUI(EditorUI editorUI) {
        this.editorUI = editorUI;
    }

    public void addWall() {
        Wall wall = new Wall();
        moveToCenter(wall);
        wall.setName("wall_" + counter++);
        selectedActor = wall;
        editorUI.setSelectedActor(selectedActor);
        stage.addActor(wall);
    }

    public void moveToCenter(SimpleActor actor) {
        Vector2 pos = stage.screenToStageCoordinates(
                new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2));
        actor.setPosition(pos.x, pos.y);
    }

    public void setSelectedActor(SimpleActor selectedActor) {
        this.selectedActor = selectedActor;
        editorUI.setSelectedActor(selectedActor);
    }
}
