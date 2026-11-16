package GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Lays out the dashboard screens (Admin Panel and Menu) on a clean, symmetric grid:
 *
 *   [ header bar ......................................................... ]
 *   Time                      [   banner image   ]                     Date
 *   SECTION TITLE                          SECTION TITLE
 *   [btn] [btn] [btn] [btn]                [btn]
 *   [btn] [btn] [btn] [btn]                [btn]
 *   [ footer bar ......................................................... ]
 *
 * It only sets positions/sizes on the designed canvas; Responsive.apply(...) then
 * scales that canvas to any screen size. Call it after initComponents() and
 * scaleImage(), before Responsive.apply(this).
 */
public final class DashboardLayout {

    private static final int MARGIN = 48;
    private static final int GAP = 16;
    private static final int SECTION_GAP = 48;
    private static final int TITLE_HEIGHT = 34;
    private static final int MAX_BUTTON_HEIGHT = 58;

    private final Container panel;
    private Component header;
    private Component footer;
    private Component banner;
    private JLabel timeCaption;
    private JLabel timeValue;
    private JLabel dateCaption;
    private JLabel dateValue;
    private final List<List<Section>> rows = new ArrayList<List<Section>>();

    private DashboardLayout(Container panel) {
        this.panel = panel;
    }

    public static DashboardLayout on(Container panel) {
        return new DashboardLayout(panel);
    }

    public DashboardLayout header(Component c) {
        header = c;
        return this;
    }

    public DashboardLayout footer(Component c) {
        footer = c;
        return this;
    }

    public DashboardLayout banner(Component c) {
        banner = c;
        return this;
    }

    public DashboardLayout time(JLabel caption, JLabel value) {
        timeCaption = caption;
        timeValue = value;
        return this;
    }

    public DashboardLayout date(JLabel caption, JLabel value) {
        dateCaption = caption;
        dateValue = value;
        return this;
    }

    /** Starts a new row of sections (sections in one row sit side by side). */
    public DashboardLayout row(Section... sections) {
        List<Section> row = new ArrayList<Section>();
        for (Section s : sections) {
            row.add(s);
        }
        rows.add(row);
        return this;
    }

    public static Section section(JLabel title, int columns, double weight, AbstractButton... buttons) {
        return new Section(title, columns, weight, buttons);
    }

    public static final class Section {
        final JLabel title;
        final int columns;
        final double weight;
        final AbstractButton[] buttons;

        Section(JLabel title, int columns, double weight, AbstractButton[] buttons) {
            this.title = title;
            this.columns = Math.max(1, columns);
            this.weight = weight;
            this.buttons = buttons;
        }

        int gridRows() {
            return (buttons.length + columns - 1) / columns;
        }
    }

    public void apply() {
        int w = panel.getWidth();
        int h = panel.getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        // Give the canvas the same shape as the screen, so scaling it up is even
        // (nothing gets stretched sideways on wide monitors).
        java.awt.Rectangle screen = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        if (screen.height > 0) {
            int target = (int) Math.round(h * (screen.width / (double) screen.height));
            if (target > w) {
                Container parent = panel.getParent();
                if (parent != null && parent.getWidth() <= w + 2) {
                    parent.setSize(target + (parent.getWidth() - w), parent.getHeight());
                }
                panel.setSize(target, h);
                w = target;
            }
        }

        int top = 0;
        if (header != null) {
            int hh = header.getHeight();
            header.setBounds(0, 0, w, hh);
            center(header);
            top = hh;
        }
        int bottom = h;
        if (footer != null) {
            int fh = footer.getHeight();
            footer.setBounds(0, h - fh, w, fh);
            center(footer);
            bottom = h - fh;
        }

        // ---- info row: time | banner | date ----
        int y = top + 28;
        int infoHeight = 70;
        if (banner != null && banner.getHeight() > 0) {
            int bw = banner.getWidth();
            int bh = banner.getHeight();
            banner.setBounds((w - bw) / 2, y, bw, bh);
            infoHeight = bh;
        }
        int infoMid = y + infoHeight / 2;
        int sideWidth = 260;
        if (timeCaption != null) {
            place(timeCaption, MARGIN, infoMid - 36, sideWidth, 32, SwingConstants.LEFT);
            place(timeValue, MARGIN, infoMid, sideWidth, 30, SwingConstants.LEFT);
        }
        if (dateCaption != null) {
            place(dateCaption, w - MARGIN - sideWidth, infoMid - 36, sideWidth, 32, SwingConstants.RIGHT);
            place(dateValue, w - MARGIN - sideWidth, infoMid, sideWidth, 30, SwingConstants.RIGHT);
        }
        y += infoHeight + 36;

        // ---- sections ----
        int totalGridRows = 0;
        for (List<Section> row : rows) {
            int max = 0;
            for (Section s : row) {
                max = Math.max(max, s.gridRows());
            }
            totalGridRows += max;
        }
        int fixed = rows.size() * (TITLE_HEIGHT + 10) + (rows.size() - 1) * SECTION_GAP + 24;
        int innerGaps = 0;
        for (List<Section> row : rows) {
            int max = 0;
            for (Section s : row) {
                max = Math.max(max, s.gridRows());
            }
            innerGaps += Math.max(0, max - 1) * GAP;
        }
        int buttonHeight = totalGridRows == 0 ? 0
                : Math.min(MAX_BUTTON_HEIGHT, (bottom - y - fixed - innerGaps) / totalGridRows);
        buttonHeight = Math.max(36, buttonHeight);

        // centre the whole block vertically in the space left
        int blockHeight = fixed - 24 + innerGaps + totalGridRows * buttonHeight;
        y += Math.max(0, (bottom - y - 24 - blockHeight) / 2);

        int contentWidth = w - 2 * MARGIN;
        for (List<Section> row : rows) {
            double weights = 0;
            for (Section s : row) {
                weights += s.weight;
            }
            int available = contentWidth - (row.size() - 1) * SECTION_GAP;
            int x = MARGIN;
            int rowGridRows = 0;
            for (Section s : row) {
                int sw = (int) Math.round(available * (s.weight / weights));
                layoutSection(s, x, y, sw, buttonHeight);
                rowGridRows = Math.max(rowGridRows, s.gridRows());
                x += sw + SECTION_GAP;
            }
            y += TITLE_HEIGHT + 10 + rowGridRows * buttonHeight + Math.max(0, rowGridRows - 1) * GAP + SECTION_GAP;
        }
    }

    private void layoutSection(Section s, int x, int y, int width, int buttonHeight) {
        if (s.title != null) {
            s.title.setOpaque(false);
            Font f = s.title.getFont();
            if (f != null) {
                s.title.setFont(f.deriveFont(Font.BOLD));
            }
            place(s.title, x, y, width, TITLE_HEIGHT, SwingConstants.LEFT);
        }
        int gy = y + TITLE_HEIGHT + 10;
        int bw = (width - (s.columns - 1) * GAP) / s.columns;
        for (int i = 0; i < s.buttons.length; i++) {
            int col = i % s.columns;
            int row = i / s.columns;
            AbstractButton b = s.buttons[i];
            if (b.getText() != null) {
                b.setText(b.getText().trim());
            }
            b.setBounds(x + col * (bw + GAP), gy + row * (buttonHeight + GAP), bw, buttonHeight);
        }
    }

    private static void place(JLabel l, int x, int y, int w, int h, int align) {
        if (l == null) {
            return;
        }
        if (l.getText() != null) {
            l.setText(l.getText().trim());
        }
        l.setHorizontalAlignment(align);
        l.setBounds(x, y, w, h);
    }

    private static void center(Component c) {
        if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            if (l.getText() != null) {
                l.setText(l.getText().trim());
            }
            l.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }
}
