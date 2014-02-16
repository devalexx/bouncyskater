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
package com.alex.bs.ui;

import com.alex.bs.managers.ResourceManager;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class EditorUI extends Table {
    private Skin skin;

    public EditorUI() {
        skin = ResourceManager.getInstance().getSkin();

        Table tableLeft = createMenuTable();

        Table tableRight = createPropertiesTable();

        Table tableBottom = createConsoleTable();

        Table topPanes = new Table();
        SplitPane leftSplitPane = new SplitPane(tableLeft, new Table(), false, skin.get("default-horizontal", SplitPane.SplitPaneStyle.class));
        leftSplitPane.setSplitAmount(0.4f);
        SplitPane rightSplitPane = new SplitPane(new Table(), tableRight, false, skin.get("default-horizontal", SplitPane.SplitPaneStyle.class));
        rightSplitPane.setSplitAmount(0.7f);
        topPanes.add(leftSplitPane).fill().expand();
        topPanes.add(rightSplitPane).fill().expand();
        topPanes.debug();

        SplitPane downSplitPane = new SplitPane(topPanes, tableBottom, true, skin.get("default-vertical", SplitPane.SplitPaneStyle.class));
        downSplitPane.setSplitAmount(0.7f);
        add(downSplitPane).fill().expand();

        /*TextButton startGameButton = new TextButton("Menu", skin.get(TextButton.TextButtonStyle.class));
        add(startGameButton);
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });*/
    }

    private Table createMenuTable() {
        return new Table();
    }

    private Table createPropertiesTable() {
        return new Table();
    }

    private Table createConsoleTable() {
        Table table = new Table();
        table.add(new ScrollPane(
                new Label("qwe\nqwe\nqwe\nqwe\nqwe\nqwe\nqwe\n", skin.get(Label.LabelStyle.class)),
                skin.get(ScrollPane.ScrollPaneStyle.class)
        )).fill().expand();

        return table;
    }
}
