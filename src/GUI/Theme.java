package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.JTextComponent;

/**
 * Modern, flat look for the whole application (pure Swing, no extra libraries).
 *
 * Theme.install() is called once at start-up (in main) before any frame is created.
 * Theme.style(frame) is called by Responsive.apply(...) for every frame and
 * modernises the colours and fonts chosen in the NetBeans designer at run time,
 * so the .form files stay unchanged and editable.
 */
public final class Theme {

    // ---- Palette -------------------------------------------------------
    public static final Color NAVY = new Color(18, 42, 76);
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_HOVER = new Color(29, 78, 216);
    public static final Color PRIMARY_PRESSED = new Color(30, 64, 175);
    public static final Color PRIMARY_LIGHT = new Color(219, 234, 254);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color DANGER_HOVER = new Color(185, 28, 28);
    public static final Color DANGER_PRESSED = new Color(153, 27, 27);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color BACKGROUND = new Color(243, 246, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color BORDER = new Color(184, 197, 214);
    public static final Color BORDER_LIGHT = new Color(226, 232, 240);
    public static final Color HOVER_LIGHT = new Color(241, 245, 249);
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color DISABLED_BG = new Color(226, 232, 240);
    public static final Color DISABLED_TEXT = new Color(148, 163, 184);

    public static final String FONT_FAMILY = pickFontFamily();
    private static final int BASE_FONT_SIZE = 14;

    private static final String VARIANT = "theme.variant";
    private static final String STYLED = "theme.styled";
    private static final String V_PRIMARY = "primary";
    private static final String V_SECONDARY = "secondary";
    private static final String V_DANGER = "danger";

    private static final Set<String> DECORATIVE_BOLD_FONTS = new HashSet<String>(Arrays.asList(
            "copperplate gothic bold", "copperplate gothic light", "broadway", "algerian",
            "cooper black", "showcard gothic", "bauhaus 93", "jokerman", "stencil"));

    private static boolean installed;

    private Theme() {
    }

    public static boolean isInstalled() {
        return installed;
    }

    private static String pickFontFamily() {
        Set<String> available = new HashSet<String>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String f : new String[]{"Segoe UI", "Inter", "Roboto", "Helvetica Neue", "Arial"}) {
            if (available.contains(f)) {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    // =====================================================================
    // Installation of the look and feel
    // =====================================================================

    /** Installs the modern look and feel. Safe to call more than once. */
    public static void install() {
        if (installed) {
            return;
        }
        installed = true;
        try {
            MetalLookAndFeel.setCurrentTheme(new ModernMetalTheme());
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        UIDefaults d = UIManager.getDefaults();

        // Custom component painters
        registerUI(d, "ButtonUI", ModernButtonUI.class);
        registerUI(d, "ScrollBarUI", ModernScrollBarUI.class);
        registerUI(d, "ComboBoxUI", ModernComboBoxUI.class);

        // Fonts: one clean family everywhere
        FontUIResource base = new FontUIResource(FONT_FAMILY, Font.PLAIN, BASE_FONT_SIZE);
        for (Object key : UIManager.getLookAndFeelDefaults().keySet().toArray()) {
            if (key != null && key.toString().endsWith(".font")) {
                d.put(key, base);
            }
        }

        // Surfaces and text
        d.put("Panel.background", res(BACKGROUND));
        d.put("Label.foreground", res(TEXT));
        d.put("CheckBox.background", res(BACKGROUND));
        d.put("RadioButton.background", res(BACKGROUND));
        d.put("OptionPane.background", res(SURFACE));
        d.put("OptionPane.messageForeground", res(TEXT));
        d.put("Viewport.background", res(SURFACE));

        // Inputs
        Border field = new BorderUIResource(new RoundBorder(8, new Insets(3, 9, 3, 9)));
        for (String k : new String[]{"TextField", "PasswordField", "FormattedTextField"}) {
            d.put(k + ".border", field);
            d.put(k + ".background", res(SURFACE));
            d.put(k + ".foreground", res(TEXT));
            d.put(k + ".selectionBackground", res(PRIMARY_LIGHT));
            d.put(k + ".selectionForeground", res(TEXT));
            d.put(k + ".caretForeground", res(PRIMARY));
            d.put(k + ".inactiveBackground", res(HOVER_LIGHT));
        }
        d.put("TextArea.selectionBackground", res(PRIMARY_LIGHT));
        d.put("TextArea.selectionForeground", res(TEXT));
        d.put("TextArea.margin", new Insets(4, 6, 4, 6));
        d.put("ComboBox.border", new BorderUIResource(new RoundBorder(8, new Insets(1, 6, 1, 2))));
        d.put("ComboBox.background", res(SURFACE));
        d.put("ComboBox.foreground", res(TEXT));
        d.put("ComboBox.selectionBackground", res(PRIMARY_LIGHT));
        d.put("ComboBox.selectionForeground", res(TEXT));
        d.put("Spinner.border", new BorderUIResource(new RoundBorder(8, new Insets(1, 4, 1, 1))));
        d.put("ScrollPane.border", new BorderUIResource(BorderFactory.createLineBorder(BORDER)));
        d.put("List.selectionBackground", res(PRIMARY_LIGHT));
        d.put("List.selectionForeground", res(TEXT));

        // Tables
        d.put("Table.background", res(SURFACE));
        d.put("Table.foreground", res(TEXT));
        d.put("Table.gridColor", res(BORDER_LIGHT));
        d.put("Table.selectionBackground", res(PRIMARY_LIGHT));
        d.put("Table.selectionForeground", res(TEXT));
        d.put("Table.focusCellHighlightBorder", new BorderUIResource(new EmptyBorder(0, 6, 0, 6)));
        d.put("TableHeader.background", res(NAVY));
        d.put("TableHeader.foreground", res(Color.WHITE));
        d.put("TableHeader.cellBorder", new BorderUIResource(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(44, 70, 108)),
                new EmptyBorder(6, 8, 6, 8))));

        // Scroll bars
        d.put("ScrollBar.width", 12);
        d.put("ScrollBar.background", res(BACKGROUND));
        d.put("ScrollBar.track", res(BACKGROUND));

        // Misc
        d.put("Button.focus", res(new Color(0, 0, 0, 0)));
        d.put("ToolTip.background", res(NAVY));
        d.put("ToolTip.foreground", res(Color.WHITE));
        d.put("ToolTip.border", new BorderUIResource(new EmptyBorder(5, 8, 5, 8)));
        d.put("TitledBorder.titleColor", res(MUTED));
        d.put("Separator.foreground", res(BORDER));
    }

    private static void registerUI(UIDefaults d, String id, Class<?> cls) {
        d.put(id, cls.getName());
        d.put(cls.getName(), cls);
    }

    private static ColorUIResource res(Color c) {
        return new ColorUIResource(c);
    }

    /** Flat Metal theme: no gradients, matching palette and fonts. */
    private static final class ModernMetalTheme extends DefaultMetalTheme {
        private final FontUIResource font = new FontUIResource(FONT_FAMILY, Font.PLAIN, BASE_FONT_SIZE);
        private final FontUIResource small = new FontUIResource(FONT_FAMILY, Font.PLAIN, 12);

        @Override public String getName() { return "Modern"; }
        @Override protected ColorUIResource getPrimary1() { return res(PRIMARY_PRESSED); }
        @Override protected ColorUIResource getPrimary2() { return res(PRIMARY); }
        @Override protected ColorUIResource getPrimary3() { return res(PRIMARY_LIGHT); }
        @Override protected ColorUIResource getSecondary1() { return res(new Color(148, 163, 184)); }
        @Override protected ColorUIResource getSecondary2() { return res(BORDER); }
        @Override protected ColorUIResource getSecondary3() { return res(BACKGROUND); }
        @Override public ColorUIResource getSystemTextColor() { return res(TEXT); }
        @Override public ColorUIResource getControlTextColor() { return res(TEXT); }
        @Override public ColorUIResource getUserTextColor() { return res(TEXT); }
        @Override public FontUIResource getControlTextFont() { return font; }
        @Override public FontUIResource getSystemTextFont() { return font; }
        @Override public FontUIResource getUserTextFont() { return font; }
        @Override public FontUIResource getMenuTextFont() { return font; }
        @Override public FontUIResource getWindowTitleFont() { return font; }
        @Override public FontUIResource getSubTextFont() { return small; }
    }

    // =====================================================================
    // Per-frame styling of designer-set colours and fonts
    // =====================================================================

    private static final FocusListener REPAINT_ON_FOCUS = new FocusAdapter() {
        @Override public void focusGained(FocusEvent e) { e.getComponent().repaint(); }
        @Override public void focusLost(FocusEvent e) { e.getComponent().repaint(); }
    };

    /** Modernises colours/fonts of a component tree built in the NetBeans designer. */
    public static void style(Component c) {
        style(c, BACKGROUND);
    }

    private static void style(Component c, Color parentBg) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            if (jc.getClientProperty(STYLED) != null) {
                return;
            }
            jc.putClientProperty(STYLED, Boolean.TRUE);
        }

        modernizeFont(c);
        modernizeBackground(c);
        Color myBg = c.isOpaque() ? c.getBackground() : parentBg;

        if (c instanceof AbstractButton && !(c instanceof JCheckBox) && !(c instanceof JRadioButton)) {
            styleButton((AbstractButton) c);
        } else if (c instanceof JCheckBox || c instanceof JRadioButton) {
            AbstractButton b = (AbstractButton) c;
            b.setOpaque(false);
            b.setFocusPainted(false);
            b.setForeground(isOnDark(parentBg) ? Color.WHITE : mapTextColor(b.getForeground()));
        } else if (c instanceof JLabel) {
            JLabel l = (JLabel) c;
            if (l.isOpaque() && near(l.getBackground(), Color.WHITE)) {
                // white "boxes" behind labels look dated; let the label sit on its parent
                l.setOpaque(false);
                myBg = parentBg;
            }
            String t = l.getText();
            if (t != null && t.startsWith("   ") && t.trim().length() > 0) {
                // text was pushed to the middle with spaces in the designer: centre it properly
                l.setText(t.trim());
                l.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            }
            boolean dark = isOnDark(l.isOpaque() ? l.getBackground() : parentBg);
            l.setForeground(dark ? Color.WHITE : mapTextColor(l.getForeground()));
        } else if (c instanceof JTextComponent) {
            c.addFocusListener(REPAINT_ON_FOCUS);
        } else if (c instanceof JTable) {
            styleTable((JTable) c);
        } else if (c instanceof JScrollPane) {
            JScrollPane sp = (JScrollPane) c;
            sp.getViewport().setBackground(SURFACE);
            if (sp.getBorder() == null || sp.getBorder() instanceof UIResource) {
                sp.setBorder(BorderFactory.createLineBorder(BORDER));
            }
        }

        if (c instanceof JComboBox || c instanceof JSpinner || c.getClass().getName().startsWith("com.toedter.")) {
            return; // these style their own inner parts
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                style(child, myBg);
            }
        }
    }

    private static void modernizeFont(Component c) {
        Font f = c.getFont();
        if (f == null || f instanceof UIResource) {
            return;
        }
        String family = f.getFamily().toLowerCase();
        String name = f.getName().toLowerCase();
        int style = f.getStyle();
        float size = f.getSize2D();
        if (DECORATIVE_BOLD_FONTS.contains(family) || DECORATIVE_BOLD_FONTS.contains(name)) {
            style = Font.BOLD;
            size = size * 0.95f;
        }
        if (!f.getFamily().equals(FONT_FAMILY)) {
            c.setFont(new Font(FONT_FAMILY, style, Math.round(size)));
        }
    }

    private static void modernizeBackground(Component c) {
        if (!c.isBackgroundSet()) {
            return;
        }
        Color b = c.getBackground();
        if (near(b, new Color(0, 204, 255)) || near(b, new Color(0, 153, 255)) || near(b, new Color(51, 204, 255))) {
            c.setBackground(NAVY); // header / footer bars
        } else if (near(b, new Color(0, 102, 255)) || near(b, new Color(51, 51, 255))) {
            c.setBackground(PRIMARY);
        } else if (near(b, new Color(255, 51, 51)) || near(b, new Color(255, 0, 0))) {
            c.setBackground(DANGER);
        } else if (c instanceof JPanel && near(b, new Color(240, 240, 240))) {
            c.setBackground(BACKGROUND);
        }
    }

    private static Color mapTextColor(Color fg) {
        if (fg == null || fg instanceof UIResource || near(fg, Color.BLACK) || near(fg, new Color(51, 51, 51))) {
            return TEXT;
        }
        if (near(fg, new Color(255, 0, 0)) || near(fg, new Color(255, 51, 51))) {
            return DANGER;
        }
        if (near(fg, new Color(51, 51, 255)) || near(fg, new Color(0, 0, 255)) || near(fg, new Color(51, 153, 255))) {
            return PRIMARY;
        }
        if (near(fg, new Color(0, 204, 51)) || near(fg, new Color(0, 153, 0))) {
            return SUCCESS;
        }
        if (near(fg, new Color(102, 102, 102)) || near(fg, new Color(153, 153, 153))) {
            return MUTED;
        }
        return fg;
    }

    private static void styleButton(AbstractButton b) {
        Color fg = b.getForeground();
        Color bg = b.isBackgroundSet() ? b.getBackground() : null;
        String variant = V_PRIMARY;
        if (near(fg, new Color(255, 0, 0)) || near(fg, new Color(255, 51, 51)) || (bg != null && near(bg, DANGER))) {
            variant = V_DANGER;
        } else if (near(fg, new Color(51, 51, 255)) || near(fg, new Color(0, 0, 255))) {
            variant = V_SECONDARY;
        }
        b.putClientProperty(VARIANT, variant);
        b.setForeground(V_SECONDARY.equals(variant) ? TEXT : Color.WHITE);
        b.setFocusPainted(false);
        b.setRolloverEnabled(true);
        b.setOpaque(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (b.getFont() != null && !b.getFont().isBold() && b.getFont().getSize() <= 20) {
            b.setFont(b.getFont().deriveFont(Font.BOLD));
        }
        Insets m = b.getMargin();
        if (m == null || m instanceof UIResource) {
            b.setMargin(new Insets(2, 8, 2, 8));
        }
    }

    private static void styleTable(JTable t) {
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(BORDER_LIGHT);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(PRIMARY_LIGHT);
        t.setSelectionForeground(TEXT);
        t.setFillsViewportHeight(true);
        if (t.getFont() != null) {
            FontMetrics fm = t.getFontMetrics(t.getFont());
            t.setRowHeight(Math.max(t.getRowHeight(), fm.getHeight() + 12));
        }
        if (t.getTableHeader() != null) {
            t.getTableHeader().setBackground(NAVY);
            t.getTableHeader().setForeground(Color.WHITE);
            Font hf = t.getTableHeader().getFont();
            if (hf != null) {
                t.getTableHeader().setFont(new Font(FONT_FAMILY, Font.BOLD, hf.getSize()));
            }
            t.getTableHeader().setReorderingAllowed(false);
        }
    }

    private static boolean isOnDark(Color bg) {
        if (bg == null) {
            return false;
        }
        double lum = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255.0;
        return lum < 0.5;
    }

    private static boolean near(Color a, Color b) {
        if (a == null || b == null) {
            return false;
        }
        return Math.abs(a.getRed() - b.getRed()) < 12
                && Math.abs(a.getGreen() - b.getGreen()) < 12
                && Math.abs(a.getBlue() - b.getBlue()) < 12;
    }

    private static Graphics2D smooth(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    // =====================================================================
    // Painters
    // =====================================================================

    /** Rounded outline for inputs; turns blue while the field has focus. */
    public static final class RoundBorder extends AbstractBorder implements UIResource {
        private final int arc;
        private final Insets pad;

        public RoundBorder(int arc, Insets pad) {
            this.arc = arc;
            this.pad = pad;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = smooth(g);
            boolean focus = c.hasFocus() || (c instanceof JComboBox && ((JComboBox<?>) c).isPopupVisible());
            if (!c.isEnabled()) {
                g2.setColor(BORDER_LIGHT);
            } else {
                g2.setColor(focus ? PRIMARY : BORDER);
            }
            g2.setStroke(new BasicStroke(focus ? 1.6f : 1f));
            g2.drawRoundRect(x, y, w - 1, h - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(pad.top, pad.left, pad.bottom, pad.right);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.set(pad.top, pad.left, pad.bottom, pad.right);
            return insets;
        }
    }

    /** Flat rounded buttons: primary (blue), secondary (outlined) and danger (red). */
    public static class ModernButtonUI extends BasicButtonUI {

        public static ComponentUI createUI(JComponent c) {
            return new ModernButtonUI();
        }

        @Override
        protected void installDefaults(AbstractButton b) {
            super.installDefaults(b);
            b.setOpaque(false);
            b.setRolloverEnabled(true);
            b.setFocusPainted(false);
            if (b.getBorder() == null || b.getBorder() instanceof UIResource) {
                b.setBorder(new BorderUIResource(new EmptyBorder(4, 12, 4, 12)));
            }
        }

        @Override
        public void paint(Graphics g, JComponent c) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel m = b.getModel();
            Object v = b.getClientProperty(VARIANT);
            String variant = v == null ? V_PRIMARY : v.toString();
            int w = c.getWidth();
            int h = c.getHeight();
            int arc = Math.min(12, h);

            Color fill;
            Color line = null;
            if (!b.isEnabled()) {
                fill = DISABLED_BG;
            } else if (V_DANGER.equals(variant)) {
                fill = m.isPressed() ? DANGER_PRESSED : m.isRollover() ? DANGER_HOVER : DANGER;
            } else if (V_SECONDARY.equals(variant)) {
                fill = m.isPressed() ? BORDER_LIGHT : m.isRollover() ? HOVER_LIGHT : SURFACE;
                line = m.isRollover() ? PRIMARY : BORDER;
            } else {
                fill = m.isPressed() ? PRIMARY_PRESSED : m.isRollover() ? PRIMARY_HOVER : PRIMARY;
            }

            Graphics2D g2 = smooth(g);
            if (b.isContentAreaFilled() || b.getText() != null && !b.getText().isEmpty()) {
                g2.setColor(fill);
                g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);
                if (line != null) {
                    g2.setColor(line);
                    g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
                }
                if (b.hasFocus() && b.isEnabled()) {
                    g2.setColor(new Color(147, 197, 253));
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, arc, arc);
                }
            }
            g2.dispose();
            super.paint(g, c);
        }

        @Override
        protected void paintText(Graphics g, AbstractButton b, java.awt.Rectangle textRect, String text) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            if (!b.isEnabled()) {
                g2.setColor(DISABLED_TEXT);
                FontMetrics fm = g2.getFontMetrics(b.getFont());
                g2.setFont(b.getFont());
                g2.drawString(text, textRect.x, textRect.y + fm.getAscent());
                return;
            }
            super.paintText(g, b, textRect, text);
        }

        @Override
        protected void paintButtonPressed(Graphics g, AbstractButton b) {
            // pressed state is painted in paint()
        }

        @Override
        protected void paintFocus(Graphics g, AbstractButton b, java.awt.Rectangle viewRect,
                java.awt.Rectangle textRect, java.awt.Rectangle iconRect) {
            // focus ring is painted in paint()
        }
    }

    /** Slim scroll bars with a rounded thumb and no arrow buttons. */
    public static class ModernScrollBarUI extends BasicScrollBarUI {

        public static ComponentUI createUI(JComponent c) {
            return new ModernScrollBarUI();
        }

        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(186, 198, 214);
            trackColor = BACKGROUND;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return zeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return zeroButton();
        }

        private JButton zeroButton() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setMinimumSize(new Dimension(0, 0));
            b.setMaximumSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, java.awt.Rectangle r) {
            g.setColor(trackColor);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, java.awt.Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = smooth(g);
            g2.setColor(isThumbRollover() || isDragging ? new Color(148, 163, 184) : thumbColor);
            int pad = 3;
            boolean vertical = scrollbar.getOrientation() == JScrollBar.VERTICAL;
            int x = r.x + (vertical ? pad : 1);
            int y = r.y + (vertical ? 1 : pad);
            int w = r.width - (vertical ? pad * 2 : 2);
            int h = r.height - (vertical ? 2 : pad * 2);
            int arc = Math.min(w, h);
            g2.fillRoundRect(x, y, w, h, arc, arc);
            g2.dispose();
        }
    }

    /** Flat combo box with a simple chevron. */
    public static class ModernComboBoxUI extends BasicComboBoxUI {

        public static ComponentUI createUI(JComponent c) {
            return new ModernComboBoxUI();
        }

        @Override
        protected void installDefaults() {
            super.installDefaults();
            comboBox.setBackground(SURFACE);
            comboBox.setOpaque(true);
        }

        @Override
        protected JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                public void paint(Graphics g) {
                    Graphics2D g2 = smooth(g);
                    g2.setColor(comboBox.isEnabled() ? MUTED : DISABLED_TEXT);
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    int s = Math.max(3, Math.min(getWidth(), getHeight()) / 6);
                    g2.drawLine(cx - s, cy - s / 2, cx, cy + s / 2);
                    g2.drawLine(cx, cy + s / 2, cx + s, cy - s / 2);
                    g2.dispose();
                }
            };
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setContentAreaFilled(false);
            b.setFocusable(false);
            b.setOpaque(false);
            return b;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle bounds, boolean hasFocus) {
            g.setColor(comboBox.isEnabled() ? SURFACE : HOVER_LIGHT);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }

        @Override
        public void paintCurrentValue(Graphics g, java.awt.Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false); // no blue block behind the selected value
        }
    }
}
