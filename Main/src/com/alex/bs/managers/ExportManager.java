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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;

public class ExportManager {
    private Stage stage;

    public ExportManager(Stage stage) {
        this.stage = stage;
    }

    public String export() {
        String accum = "function onCreate()\n";

        for(Actor a : stage.getRoot().getChildren()) {
            if(!(a instanceof SimpleActor))
                continue;

            SimpleActor sa = (SimpleActor) a;
            accum += exportActor(sa);
        }

        accum += "end\n" +
                "function onBeginContact(contact)\n" +
                "    if contact:getFixtureA():getBody() == player:getBody() and\n" +
                "            contact:getFixtureB():getBody() == obj:getBody()\n" +
                "            or\n" +
                "            contact:getFixtureB():getBody() == obj:getBody() and\n" +
                "            contact:getFixtureA():getBody() == player:getBody() then\n" +
                "        stage:removeActor(obj)\n" +
                "    end\n" +
                "end\n" +
                "\n" +
                "function onEndContact(contact)\n" +
                "end" +
                "\n" +
                "function onCheck()\n" +
                "    return false\n" +
                "end";



        return accum;
    }

    public String exportActor(SimpleActor sa) {
        String s = "";
        String name = sa.getName() == null ? "obj" : sa.getName();
        switch(sa.getType()) {
            case WALL:
                s += "    " + name + " = luajava.new(Wall)\n" +
                        "    " + name + ":setSpriteAndBodyBox(" + sa.getWidth() + ", " + sa.getHeight() + ")\n";
                break;
            case PLAYER:
                s += "    " + name + " = luajava.new(Player)\n";
                break;
            case SKATE:
                s += "    " + name + " = luajava.new(Skate)\n";
                break;
            case COIN:
                s += "    " + name + " = luajava.new(Coin)\n";
                break;
            case MESH:
                s += "    " + name + " = luajava.new(Mesh)\n";
                for(Vector2 v : ((Mesh)sa).getVertices())
                s += "    " + name + ":addVertex(" + v.x + ", " + v.y + ")\n";
                break;
        }

        if(s.isEmpty())
            return "    --Error import " + sa + "\n\n";
        else {
            s += "    " + name + ":setPosition(" + (sa.getX() + sa.getWidth() / 2) + ", " +
                    (sa.getY() + sa.getHeight() / 2) + ")\n";
            s += "    " + name + ":setRotation(" + sa.getRotation() + ")\n";
            if(sa.getName() != null)
                s += "    " + name + ":setName('" + sa.getName() + "')\n\n";
            s += "    stage:addActor(" + name + ")\n\n";
            return s;
        }
    }
}
