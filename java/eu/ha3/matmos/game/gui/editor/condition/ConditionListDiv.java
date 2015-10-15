package eu.ha3.matmos.game.gui.editor.condition;

import com.google.common.base.Optional;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.gui.editor.div.Div;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionListDiv extends Div
{
    private final Map<String, ConditionSet> conditionSets = new HashMap<String, ConditionSet>();
    private final ConditionEditorDiv editorDiv;

    private String selected = "";
    private int left = 0;
    private int right = 0;
    private int top = 0;
    private int bottom = 0;

    public ConditionListDiv(ConditionEditorDiv editor, float width, float height, float marginLeft, float marginTop)
    {
        super(width, height, marginLeft, marginTop);
        editorDiv = editor;
        conditionSets.putAll(MAtmos.dataRegistry.conditions);
    }

    @Override
    public void onDraw(int mouseX, int mouseY, int left, int top, int right, int bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        for (String s : conditionSets.keySet())
        {
            MCGame.drawString(s, left + 5, top + 6, 0xFFFFFFFF);
            drawHorizontalLine(left + 1, right - 2, top + ConditionEditorDiv.lineHeight, 0xAAFFFFFF);
            if (s.equals(selected))
            {
                drawRect(left, top, right, top + ConditionEditorDiv.lineHeight, 0x33FFFFFF);
            }
            else if (mouseOver(mouseX, mouseY, left, top, right, top + ConditionEditorDiv.lineHeight))
            {
                drawRect(left, top, right, top + ConditionEditorDiv.lineHeight, 0x22FFFFFF);
            }
            top += (ConditionEditorDiv.lineHeight + 1);
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button)
    {
        if (mouseOver(mouseX, mouseY, left, top, right, bottom))
        {
            for (String s : conditionSets.keySet())
            {
                if (mouseOver(mouseX, mouseY, left, top, right, top + ConditionEditorDiv.lineHeight))
                {
                    if (!s.equals(selected))
                    {
                        selected = s;
                        editorDiv.setConditionSet(conditionSets.get(s));
                    }
                    return;
                }
                top += (ConditionEditorDiv.lineHeight + 1);
            }
            selected = "";
            Optional<ConditionSet> optional = editorDiv.clearCurrent();
            if (optional.isPresent())
            {
                ConditionSet set = optional.get();
                conditionSets.put(set.getName(), set);
            }
        }
    }

    @Override
    public void onKeyType(char c, int code)
    {

    }
}
