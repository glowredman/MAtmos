package eu.ha3.matmos.game.gui.editor.condition;

import com.google.common.base.Optional;
import eu.ha3.matmos.engine.condition.Checkable;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.gui.editor.div.Div;
import eu.ha3.matmos.game.gui.editor.element.Scrollbar;
import eu.ha3.matmos.game.gui.editor.element.TextField;
import eu.ha3.matmos.util.Timer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionEditorDiv extends Div
{
    private final List<ConditionField> lines = new ArrayList<ConditionField>();
    private final Timer timer = new Timer();
    private final Scrollbar scrollbar = new Scrollbar();

    protected static int lineHeight = 20;

    private TextField conditionName = new TextField();

    private int editorTop = 0;
    private int editorLeft = 0;
    private int editorRight = 0;
    private int editorBottom = 0;
    private int contentHeight = 0;

    private boolean cursor = false;

    public ConditionEditorDiv(float w, float h, float ml, float mt)
    {
        super(w, h, ml, mt);
    }

    private boolean validName()
    {
        return !conditionName.empty() && !conditionName.getString().equals("Nothing Selected");
    }

    public Optional<ConditionSet> clearCurrent()
    {
        if (!validName())
        {
            conditionName.setActive(false);
            return Optional.absent();
        }
        ConditionSet result = new ConditionSet(conditionName.getString());
        for (ConditionField cb : lines)
        {
            if (cb.valid())
            {
                result.addCondition(cb.get());
            }
        }
        conditionName.setText("Nothing Selected");
        lines.clear();
        return Optional.of(result);
    }

    public ConditionEditorDiv newConditionSet()
    {
        lines.clear();
        conditionName.setText("");
        conditionName.setActive(true);
        return this;
    }

    public ConditionEditorDiv setConditionSet(ConditionSet set)
    {
        lines.clear();
        conditionName.setText(set.getName());
        for (Checkable c : set.getConditions())
        {
            lines.add(new ConditionField(c.serialize()));
        }
        return this;
    }

    public boolean active()
    {
        if (conditionName.active())
        {
            return true;
        }
        for (ConditionField cf : lines)
        {
            if (cf.active())
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDraw(int mouseX, int mouseY, int left, int top, int right, int bottom)
    {
        drawBorderedBox(left, top + 19, right, bottom, 0xDD666666, 0xFFFFFFFF);

        editorTop = top + 20;
        editorLeft = left + 1;
        editorRight = right - 1;
        editorBottom = bottom;

        int currentTop = top + 20;

        if (contentHeight > bottom)
        {
            scrollbar.draw(mouseX, mouseY, right, editorTop, bottom);
            currentTop -= scrollbar.getContentTop(contentHeight, editorTop, bottom);
        }

        contentHeight = editorTop;
        List<ConditionField> empty = new ArrayList<ConditionField>();
        for (ConditionField cf : lines)
        {
            if (cf.empty() && !cf.active())
            {
                empty.add(cf);
            }
            else
            {
                if (timer.getPeriodMs() > 500)
                {
                    timer.punchIn();
                    cursor = !cursor;
                }
                cf.setHovered(mouseOver(mouseX, mouseY, editorLeft, currentTop, editorRight, currentTop + lineHeight));
                cf.drawBox(editorLeft, currentTop, editorRight, currentTop + lineHeight);
                cf.draw(cursor, left + 5, currentTop + 6, editorRight);
                drawHorizontalLine(editorLeft + 1, editorRight - 2, currentTop + lineHeight, 0xAAFFFFFF);
                currentTop += (lineHeight + 1);
                contentHeight += (lineHeight + 1);
            }
        }
        contentHeight += (lineHeight + 1);

        if (timer.getPeriodMs() > 500)
        {
            timer.punchIn();
            cursor = !cursor;
        }

        drawBorderedBox(left, top, right, top + lineHeight, 0xFF333333, 0xFFFFFFFF);
        MCGame.drawString(conditionName.getString(cursor), left + 5, top + 6, 0xFFFFFFFF);

        contentHeight += (lineHeight + 1);
        lines.removeAll(empty);
        timer.punchOut();
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button)
    {
        scrollbar.onClick(mouseX, mouseY, editorRight);
        if (mouseOver(mouseX, mouseY, editorLeft, editorTop, editorRight, editorBottom) && validName())
        {
            conditionName.setActive(false);
            boolean noneSelected = true;
            for (ConditionField cf : lines)
            {
                if (cf.hovered())
                {
                    cf.setActive(noneSelected);
                    noneSelected = false;
                }
                else if (cf.active())
                {
                    cf.setActive(false);
                }
            }
            if (noneSelected)
            {
                ConditionField cf = new ConditionField();
                cf.setActive(true);
                lines.add(cf);
            }
        }
        else
        {
            for (ConditionField cf : lines)
                cf.setActive(false);
        }

    }

    @Override
    public void onMouseRelease()
    {
        scrollbar.onMouseRelease();
    }

    @Override
    public void onKeyType(char c, int code)
    {
        if (conditionName.active() && c != ' ')
        {
            conditionName.onKeyType(c, code);
        }
        else
        {
            for (ConditionField cf : lines)
            {
                if (cf.active())
                {
                    cf.onKeyType(c, code);
                    return;
                }
            }
        }
    }
}
