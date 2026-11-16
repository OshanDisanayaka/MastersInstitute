package GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import javax.swing.AbstractButton;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.JTextComponent;

/**
 * Makes a NetBeans-designed JFrame responsive without touching the
 * generated GUI code, so the forms stay editable in the NetBeans designer.
 *
 * Usage: call {@code Responsive.apply(this);} at the end of a frame's constructor.
 *
 * What it does:
 *  - Records the size/position of every component exactly as designed in NetBeans.
 *  - Replaces the fixed layouts (GroupLayout / AbsoluteLayout / null) with a layout
 *    that stretches every component proportionally when the window is resized.
 *  - Scales fonts, table row heights and images along with the window.
 *  - Applies the modern Theme, makes the frame resizable, fills the screen with
 *    large windows and keeps every window above the Windows taskbar.
 */
public final class Responsive {

    private static final String DESIGN_BOUNDS = "responsive.designBounds";
    private static final String BASE_FONT = "responsive.baseFont";
    private static final String SCALED_FONT = "responsive.scaledFont";
    private static final String FITTED_FONT = "responsive.fittedFont";
    private static final String BASE_ICON = "responsive.baseIcon";
    private static final String SCALED_ICON = "responsive.scaledIcon";
    private static final String BASE_ROW_HEIGHT = "responsive.baseRowHeight";

    /** Windows wider/taller than this share of the screen open maximized. */
    private static final double MAXIMIZE_THRESHOLD = 0.6;
    /** Smallest size a window can be shrunk to, relative to its designed size. */
    private static final double MIN_SIZE_FACTOR = 0.5;

    private Responsive() {
    }

    public static void apply(JFrame frame) {
        if (!Theme.isInstalled()) {
            // Frame was opened without going through main(): switch it to the theme now.
            Theme.install();
            SwingUtilities.updateComponentTreeUI(frame);
            frame.pack();
        }
        Theme.style(frame.getContentPane());

        Container content = frame.getContentPane();
        if (content.getWidth() <= 0 || content.getHeight() <= 0) {
            frame.pack();
        }
        Dimension designFrameSize = frame.getSize();

        prepare(content, true);

        if (!frame.isUndecorated()) {
            frame.setResizable(true);
        }
        frame.setMinimumSize(new Dimension(
                (int) (designFrameSize.width * MIN_SIZE_FACTOR),
                (int) (designFrameSize.height * MIN_SIZE_FACTOR)));

        // Work area = screen minus the Windows taskbar, so nothing is hidden behind it.
        Rectangle area = usableScreenArea(frame);
        boolean large = designFrameSize.width > area.width * MAXIMIZE_THRESHOLD
                || designFrameSize.height > area.height * MAXIMIZE_THRESHOLD;
        if (large && frame.isResizable()) {
            frame.setBounds(area);
        } else {
            int w = Math.min(designFrameSize.width, area.width);
            int h = Math.min(designFrameSize.height, area.height);
            frame.setBounds(area.x + (area.width - w) / 2, area.y + (area.height - h) / 2, w, h);
        }
        frame.validate();
    }

    private static Rectangle usableScreenArea(JFrame frame) {
        GraphicsConfiguration gc = frame.getGraphicsConfiguration();
        if (gc == null) {
            gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        }
        Rectangle b = gc.getBounds();
        Insets in = Toolkit.getDefaultToolkit().getScreenInsets(gc);
        return new Rectangle(b.x + in.left, b.y + in.top,
                b.width - in.left - in.right, b.height - in.top - in.bottom);
    }

    /** Records designed bounds and swaps in the scaling layout, recursively. */
    private static void prepare(Container container, boolean root) {
        if (container.getWidth() <= 0 || container.getHeight() <= 0) {
            container.setSize(container.getPreferredSize());
            container.doLayout();
        }
        Component[] children = container.getComponents();
        Rectangle[] bounds = new Rectangle[children.length];
        for (int i = 0; i < children.length; i++) {
            bounds[i] = children[i].getBounds();
        }

        tidyAlignment(children, bounds);
        Dimension design = fitDesignSize(container.getSize(), children, bounds);

        for (int i = 0; i < children.length; i++) {
            Component child = children[i];
            if (child instanceof JComponent) {
                ((JComponent) child).putClientProperty(DESIGN_BOUNDS, bounds[i]);
            }
            if (child instanceof Container && isScalable((Container) child)) {
                prepare((Container) child, false);
            }
        }
        container.setLayout(new ScaleLayout(design, root));
    }

    /**
     * If components stick out past the edge of their panel (they were cut off in the
     * original design), the panel's design size is enlarged so everything fits on screen.
     */
    private static Dimension fitDesignSize(Dimension size, Component[] children, Rectangle[] bounds) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < children.length; i++) {
            Rectangle r = bounds[i];
            if (!children[i].isVisible() || r.width <= 0 || r.height <= 0) {
                continue;
            }
            minX = Math.min(minX, r.x);
            minY = Math.min(minY, r.y);
            maxX = Math.max(maxX, r.x + r.width);
            maxY = Math.max(maxY, r.y + r.height);
        }
        if (minX == Integer.MAX_VALUE) {
            return size;
        }
        int w = size.width;
        int h = size.height;
        if (maxX > w) {
            w = maxX + clamp(minX, 8, 30);
        }
        if (maxY > h) {
            h = maxY + clamp(minY, 8, 30);
        }
        return new Dimension(w, h);
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    // ---- Automatic alignment of form columns -------------------------------

    private static final int ALIGN_TOLERANCE = 45;

    /**
     * Lines up labels and input fields that are "almost" in the same column
     * (a few pixels off in the NetBeans design). Left edges of labels snap together,
     * left and right edges of input fields snap together. A move is skipped if it
     * would make the component overlap something it did not overlap before.
     */
    private static void tidyAlignment(Component[] children, Rectangle[] bounds) {
        snapEdges(children, bounds, 0, true);   // labels: left edges
        snapEdges(children, bounds, 1, true);   // inputs: left edges
        snapEdges(children, bounds, 1, false);  // inputs: right edges (width)
        snapEdges(children, bounds, 2, true);   // check boxes / radio buttons: left edges
    }

    /** 0 = label, 1 = input field, 2 = check box / radio, -1 = leave alone. */
    private static int alignGroup(Component c) {
        if (c instanceof JLabel) {
            return ((JLabel) c).getIcon() == null ? 0 : -1;
        }
        if (c instanceof JCheckBox || c instanceof JRadioButton) {
            return 2;
        }
        if (c instanceof JTextComponent || c instanceof JComboBox || c instanceof JSpinner
                || c.getClass().getName().startsWith("com.toedter.")) {
            return 1;
        }
        if (c instanceof javax.swing.JScrollPane) {
            Component view = ((javax.swing.JScrollPane) c).getViewport().getView();
            return view instanceof JTextComponent ? 1 : -1; // text areas, not tables
        }
        return -1;
    }

    private static void snapEdges(Component[] children, Rectangle[] bounds, int group, boolean leftEdge) {
        java.util.List<Integer> idx = new java.util.ArrayList<Integer>();
        for (int i = 0; i < children.length; i++) {
            if (children[i].isVisible() && bounds[i].width > 0 && alignGroup(children[i]) == group) {
                idx.add(i);
            }
        }
        if (idx.size() < 3) {
            return;
        }
        // sort by edge position
        final Rectangle[] b = bounds;
        final boolean left = leftEdge;
        java.util.Collections.sort(idx, new java.util.Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer c) {
                return Integer.compare(edge(b[a], left), edge(b[c], left));
            }
        });
        int start = 0;
        while (start < idx.size()) {
            int end = start;
            int first = edge(bounds[idx.get(start)], leftEdge);
            while (end + 1 < idx.size() && edge(bounds[idx.get(end + 1)], leftEdge) - first <= ALIGN_TOLERANCE) {
                end++;
            }
            if (end > start) {
                int target = modeEdge(bounds, idx.subList(start, end + 1), leftEdge);
                if (target != Integer.MIN_VALUE) {
                    for (int k = start; k <= end; k++) {
                        int i = idx.get(k);
                        moveEdge(children, bounds, i, target, leftEdge);
                    }
                }
            }
            start = end + 1;
        }
    }

    private static int edge(Rectangle r, boolean left) {
        return left ? r.x : r.x + r.width;
    }

    /** Most common edge position in the cluster (needs at least 2 equal values). */
    private static int modeEdge(Rectangle[] bounds, java.util.List<Integer> cluster, boolean left) {
        int best = Integer.MIN_VALUE;
        int bestCount = 1;
        for (int i : cluster) {
            int e = edge(bounds[i], left);
            int count = 0;
            for (int j : cluster) {
                if (Math.abs(edge(bounds[j], left) - e) <= 2) {
                    count++;
                }
            }
            if (count > bestCount || (count == bestCount && best != Integer.MIN_VALUE && e < best)) {
                best = e;
                bestCount = count;
            }
        }
        return best;
    }

    private static void moveEdge(Component[] children, Rectangle[] bounds, int i, int target, boolean leftEdge) {
        Rectangle old = bounds[i];
        Rectangle moved;
        if (leftEdge) {
            if (old.x == target) {
                return;
            }
            moved = new Rectangle(target, old.y, old.width, old.height);
        } else {
            int newWidth = target - old.x;
            if (newWidth == old.width || newWidth < old.width / 2) {
                return;
            }
            moved = new Rectangle(old.x, old.y, newWidth, old.height);
        }
        for (int j = 0; j < bounds.length; j++) {
            if (j == i || !children[j].isVisible()) {
                continue;
            }
            if (moved.intersects(bounds[j]) && !old.intersects(bounds[j])) {
                return; // would bump into a neighbour: keep the original position
            }
        }
        bounds[i] = moved;
    }

    /** Only plain designer panels are rescaled; library components keep their own layout. */
    private static boolean isScalable(Container c) {
        if (!(c instanceof JPanel) && !(c instanceof JLayeredPane)) {
            return false;
        }
        LayoutManager lm = c.getLayout();
        return lm == null
                || lm instanceof GroupLayout
                || lm.getClass().getName().endsWith("AbsoluteLayout");
    }

    private static final class ScaleLayout implements LayoutManager2 {

        private final Dimension design;
        private final boolean root;
        private double lastFontScale = -1;

        ScaleLayout(Dimension design, boolean root) {
            this.design = new Dimension(Math.max(1, design.width), Math.max(1, design.height));
            this.root = root;
        }

        @Override
        public void layoutContainer(Container parent) {
            double sx = parent.getWidth() / (double) design.width;
            double sy = parent.getHeight() / (double) design.height;
            if (sx <= 0 || sy <= 0) {
                return;
            }
            if (root) {
                double fontScale = Math.min(sx, sy);
                if (Math.abs(fontScale - lastFontScale) > 0.005) {
                    lastFontScale = fontScale;
                    scaleFonts(parent, fontScale);
                }
            }
            for (Component child : parent.getComponents()) {
                Rectangle r = designBounds(child, sx, sy);
                int x = (int) Math.round(r.x * sx);
                int y = (int) Math.round(r.y * sy);
                int x2 = (int) Math.round((r.x + r.width) * sx);
                int y2 = (int) Math.round((r.y + r.height) * sy);
                child.setBounds(x, y, Math.max(0, x2 - x), Math.max(0, y2 - y));
                scaleIcon(child, r);
                fitText(child);
            }
        }

        private Rectangle designBounds(Component child, double sx, double sy) {
            if (child instanceof JComponent) {
                Object o = ((JComponent) child).getClientProperty(DESIGN_BOUNDS);
                if (o instanceof Rectangle) {
                    return (Rectangle) o;
                }
                // Component added after start-up: derive its design bounds once.
                Rectangle b = child.getBounds();
                Rectangle r = new Rectangle((int) (b.x / sx), (int) (b.y / sy),
                        (int) (b.width / sx), (int) (b.height / sy));
                ((JComponent) child).putClientProperty(DESIGN_BOUNDS, r);
                return r;
            }
            return child.getBounds();
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return new Dimension(design);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return new Dimension((int) (design.width * MIN_SIZE_FACTOR), (int) (design.height * MIN_SIZE_FACTOR));
        }

        @Override
        public Dimension maximumLayoutSize(Container target) {
            return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void addLayoutComponent(Component comp, Object constraints) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public float getLayoutAlignmentX(Container target) {
            return 0.5f;
        }

        @Override
        public float getLayoutAlignmentY(Container target) {
            return 0.5f;
        }

        @Override
        public void invalidateLayout(Container target) {
        }
    }

    /** Scales fonts (and table row heights) of the whole component tree. */
    private static void scaleFonts(Component c, double scale) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Font current = jc.getFont();
            if (current != null) {
                Font base = (Font) jc.getClientProperty(BASE_FONT);
                if (base == null || (current != jc.getClientProperty(SCALED_FONT)
                        && current != jc.getClientProperty(FITTED_FONT))) {
                    base = current; // first time, or the program changed the font itself
                    jc.putClientProperty(BASE_FONT, base);
                }
                Font scaled = base.deriveFont((float) Math.max(6, base.getSize2D() * scale));
                jc.setFont(scaled);
                jc.putClientProperty(SCALED_FONT, scaled);
            }
            if (jc instanceof JTable) {
                JTable t = (JTable) jc;
                Integer baseRow = (Integer) t.getClientProperty(BASE_ROW_HEIGHT);
                if (baseRow == null) {
                    baseRow = t.getRowHeight();
                    t.putClientProperty(BASE_ROW_HEIGHT, baseRow);
                }
                t.setRowHeight(Math.max(1, (int) Math.round(baseRow * scale)));
            }
        }
        if (c instanceof JSpinner) {
            // The spinner's text field follows the spinner's font.
            JComponent editor = ((JSpinner) c).getEditor();
            if (editor instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) editor).getTextField().setFont(new FontUIResource(c.getFont()));
            }
            return;
        }
        if (c.getClass().getName().startsWith("com.toedter.")) {
            setTextFieldFonts((Container) c, c.getFont()); // date / year pickers
            return;
        }
        if (c instanceof JComboBox) {
            return; // passes its font on to its own inner parts
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                scaleFonts(child, scale);
            }
        }
    }

    private static void setTextFieldFonts(Container c, Font f) {
        for (Component child : c.getComponents()) {
            if (child instanceof JTextComponent) {
                child.setFont(f);
            } else if (child instanceof Container) {
                setTextFieldFonts((Container) child, f);
            }
        }
    }

    /** Shrinks the font of a button/label/check box a little if its text would be cut off ("..."). */
    private static void fitText(Component c) {
        String text;
        Icon icon;
        if (c instanceof AbstractButton) {
            text = ((AbstractButton) c).getText();
            icon = ((AbstractButton) c).getIcon();
        } else if (c instanceof JLabel) {
            text = ((JLabel) c).getText();
            icon = ((JLabel) c).getIcon();
        } else {
            return;
        }
        if (text == null || text.isEmpty() || text.startsWith("<html") || c.getWidth() <= 0) {
            return;
        }
        JComponent jc = (JComponent) c;
        Font base = (Font) jc.getClientProperty(BASE_FONT);
        Font current = jc.getFont();
        if (current == null) {
            return;
        }
        // start again from the normal scaled size, then shrink only as much as needed
        Font scaled = (Font) jc.getClientProperty(SCALED_FONT);
        Font f = scaled != null ? scaled : current;
        Insets in = jc.getInsets();
        int extra = in.left + in.right + 4;
        if (c instanceof AbstractButton && !(c instanceof JCheckBox) && !(c instanceof JRadioButton)) {
            Insets m = ((AbstractButton) c).getMargin();
            if (m != null) {
                extra += m.left + m.right;
            }
        }
        if (icon != null) {
            extra += icon.getIconWidth() + 4;
        }
        if (c instanceof JCheckBox || c instanceof JRadioButton) {
            extra += 22; // the box/circle
        }
        int available = c.getWidth() - extra;
        float minSize = (base != null ? base.getSize2D() : f.getSize2D()) * 0.55f;
        Font fitted = f;
        while (jc.getFontMetrics(fitted).stringWidth(text) > available && fitted.getSize2D() - 0.5f >= minSize) {
            fitted = fitted.deriveFont(fitted.getSize2D() - 0.5f);
        }
        if (!fitted.equals(current)) {
            jc.setFont(fitted);
        }
        jc.putClientProperty(FITTED_FONT, fitted);
    }

    /** Resizes images on labels/buttons: background images stretch, logos keep their shape. */
    private static void scaleIcon(Component c, Rectangle design) {
        Icon current;
        if (c instanceof JLabel) {
            current = ((JLabel) c).getIcon();
        } else if (c instanceof AbstractButton) {
            current = ((AbstractButton) c).getIcon();
        } else {
            return;
        }
        if (!(current instanceof ImageIcon) || design.width <= 0 || design.height <= 0) {
            return;
        }
        JComponent jc = (JComponent) c;
        ImageIcon base = (ImageIcon) jc.getClientProperty(BASE_ICON);
        if (base == null || current != jc.getClientProperty(SCALED_ICON)) {
            base = (ImageIcon) current;
            jc.putClientProperty(BASE_ICON, base);
        }
        int iw = base.getIconWidth();
        int ih = base.getIconHeight();
        if (iw <= 0 || ih <= 0 || c.getWidth() <= 0 || c.getHeight() <= 0) {
            return;
        }
        double rx = c.getWidth() / (double) design.width;
        double ry = c.getHeight() / (double) design.height;
        boolean fill = iw >= design.width * 0.8 && ih >= design.height * 0.8;
        int nw;
        int nh;
        if (fill) {
            nw = (int) Math.round(iw * rx);
            nh = (int) Math.round(ih * ry);
        } else {
            double s = Math.min(rx, ry);
            nw = (int) Math.round(iw * s);
            nh = (int) Math.round(ih * s);
        }
        if (nw <= 0 || nh <= 0) {
            return;
        }
        ImageIcon scaled;
        if (nw == iw && nh == ih) {
            scaled = base;
        } else {
            if (current.getIconWidth() == nw && current.getIconHeight() == nh) {
                return; // already the right size
            }
            scaled = new ImageIcon(base.getImage().getScaledInstance(nw, nh, Image.SCALE_SMOOTH));
        }
        jc.putClientProperty(SCALED_ICON, scaled);
        if (c instanceof JLabel) {
            ((JLabel) c).setIcon(scaled);
        } else {
            ((AbstractButton) c).setIcon(scaled);
        }
    }
}
