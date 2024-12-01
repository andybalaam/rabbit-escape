package rabbitescape.ui.swing;

import static org.junit.Assert.*;
import static rabbitescape.ui.swing.SwingConfigSetup.CFG_DARK_THEME;

import org.junit.Test;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Locale;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import rabbitescape.engine.config.Config;
import rabbitescape.engine.config.ConfigTools;
import rabbitescape.render.BitmapCache;
import rabbitescape.render.BitmapLoader;
import rabbitescape.render.BitmapScaler;
import rabbitescape.render.androidlike.Sound;

/**
 * Theme 클래스 및 관련 클래스들의 기능을 세세하게 테스트하는 클래스입니다.
 */
public class TestTheme {

    // Theme 클래스의 getInstance() 메소드 테스트
    @Test
    public void testGetInstance() {
        // DarkTheme의 인스턴스를 가져온다.
        Theme darkTheme1 = DarkTheme.getInstance();
        Theme darkTheme2 = DarkTheme.getInstance();

        // 두 인스턴스가 동일한지 확인한다 (싱글톤 패턴 검증)
        assertSame("DarkTheme.getInstance()는 동일한 인스턴스를 반환해야 합니다.", darkTheme1, darkTheme2);

        // BrightTheme의 인스턴스를 가져온다.
        Theme brightTheme1 = BrightTheme.getInstance();
        Theme brightTheme2 = BrightTheme.getInstance();

        // 두 인스턴스가 동일한지 확인한다 (싱글톤 패턴 검증)
        assertSame("BrightTheme.getInstance()는 동일한 인스턴스를 반환해야 합니다.", brightTheme1, brightTheme2);

        // 서로 다른 테마의 인스턴스가 동일하지 않은지 확인한다.
        assertNotSame("DarkTheme와 BrightTheme의 인스턴스는 달라야 합니다.", darkTheme1, brightTheme1);
    }

    // getOppositeTheme() 메소드 테스트
    @Test
    public void testGetOppositeTheme() {
        // DarkTheme의 인스턴스를 가져온다.
        Theme darkTheme = DarkTheme.getInstance();

        // DarkTheme의 반대 테마를 가져온다.
        Theme oppositeTheme = darkTheme.getOppositeTheme();

        // 반대 테마가 BrightTheme인지 확인한다.
        assertTrue("DarkTheme의 반대 테마는 BrightTheme이어야 합니다.", oppositeTheme instanceof BrightTheme);

        // BrightTheme의 인스턴스를 가져온다.
        Theme brightTheme = BrightTheme.getInstance();

        // BrightTheme의 반대 테마를 가져온다.
        Theme oppositeOfBright = brightTheme.getOppositeTheme();

        // 반대 테마가 DarkTheme인지 확인한다.
        assertTrue("BrightTheme의 반대 테마는 DarkTheme이어야 합니다.", oppositeOfBright instanceof DarkTheme);

        // 서로의 반대 테마가 원래 테마인지 확인한다.
        assertSame("BrightTheme의 반대 테마의 반대는 BrightTheme이어야 합니다.", brightTheme, oppositeOfBright.getOppositeTheme());
        assertSame("DarkTheme의 반대 테마의 반대는 DarkTheme이어야 합니다.", darkTheme, oppositeTheme.getOppositeTheme());
    }

    // getBackgroundColor(), getGraphPaperMajor(), getGraphPaperMinor(), getWaterColor() 메소드 테스트
    @Test
    public void testColorGetters() {
        // DarkTheme의 인스턴스를 가져온다.
        DarkTheme darkTheme = (DarkTheme) DarkTheme.getInstance();

        // 각 색상 값을 가져온다.
        Color backgroundColor = darkTheme.getBackgroundColor();
        Color graphPaperMajor = darkTheme.getGraphPaperMajor();
        Color graphPaperMinor = darkTheme.getGraphPaperMinor();
        Color waterColor = darkTheme.getWaterColor();

        // 예상되는 색상과 비교한다.
        assertEquals("DarkTheme의 배경색은 Color.LIGHT_GRAY이어야 합니다.", Color.LIGHT_GRAY, backgroundColor);
        assertEquals("DarkTheme의 graphPaperMajor 색상은 (210,210,210)이어야 합니다.",
            new Color(210, 210, 210), graphPaperMajor);
        assertEquals("DarkTheme의 graphPaperMinor 색상은 (200,200,200)이어야 합니다.",
            new Color(200, 200, 200), graphPaperMinor);
        assertEquals("DarkTheme의 waterColor는 (24,50,100)이어야 합니다.",
            new Color(24, 50, 100), waterColor);
    }

    // setSideMenuColor(SideMenu sideMenu) 메소드 테스트
    @Test
    public void testSetSideMenuColor() {
        // DarkTheme의 인스턴스를 가져온다.
        Theme darkTheme = DarkTheme.getInstance();

        // 필요한 객체들을 생성한다.
        Container contentPane = new JPanel();
        Dimension buttonSizeInPixels = new Dimension(32, 32);
        Config uiConfig = SwingConfigSetup.createConfig();
        Color backgroundColor = Color.WHITE; // 임의의 색상

        // BitmapCache 생성
        FakeBitmapLoader loader = new FakeBitmapLoader();
        FakeBitmapScaler scaler = new FakeBitmapScaler();
        BitmapCache<SwingBitmap> bitmapCache = new BitmapCache<SwingBitmap>(
            loader, scaler, Runtime.getRuntime().maxMemory() / 8 );

        // SideMenu를 생성한다.
        SideMenu sideMenu = new SideMenu(contentPane, bitmapCache, buttonSizeInPixels, uiConfig, backgroundColor);

        // sideMenu의 배경색을 초기화한다.
        sideMenu.setPanelBackground(Color.RED);

        // darkTheme을 사용하여 sideMenu의 색상을 설정한다.
        darkTheme.setSideMenuColor(sideMenu);

        // sideMenu의 배경색을 가져오기 위해 리플렉션을 사용한다.
        Color actualColor = getSideMenuPanelBackgroundColor(sideMenu);

        // sideMenu의 배경색이 darkTheme의 배경색과 동일한지 확인한다.
        assertEquals("SideMenu의 배경색은 DarkTheme의 배경색과 동일해야 합니다.",
            darkTheme.getBackgroundColor(), actualColor);
    }

    // SideMenu의 panel 필드가 private이므로, 리플렉션을 사용하여 배경색을 가져온다.
    private Color getSideMenuPanelBackgroundColor(SideMenu sideMenu) {
        try {
            Field panelField = SideMenu.class.getDeclaredField("panel");
            panelField.setAccessible(true);
            JPanel panel = (JPanel) panelField.get(sideMenu);
            return panel.getBackground();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("SideMenu의 panel 필드에 접근할 수 없습니다.");
            return null;
        }
    }

    // setColorMenuUi(Container contentPane, JScrollPane scrollPane, JPanel menuPanel) 메소드 테스트
    @Test
    public void testSetColorMenuUi() {
        // DarkTheme의 인스턴스를 가져온다.
        Theme darkTheme = DarkTheme.getInstance();

        // 컨테이너와 패널들을 생성한다.
        Container contentPane = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        JPanel menuPanel = new JPanel();

        // 각 패널의 배경색을 초기화한다.
        contentPane.setBackground(Color.RED);
        scrollPane.setBackground(Color.GREEN);
        menuPanel.setBackground(Color.BLUE);

        // darkTheme을 사용하여 색상을 설정한다.
        darkTheme.setColorMenuUi(contentPane, scrollPane, menuPanel);

        // 각 패널의 배경색이 darkTheme의 배경색과 동일한지 확인한다.
        assertEquals("contentPane의 배경색은 DarkTheme의 배경색과 동일해야 합니다.",
            darkTheme.getBackgroundColor(), contentPane.getBackground());
        assertEquals("scrollPane의 배경색은 DarkTheme의 배경색과 동일해야 합니다.",
            darkTheme.getBackgroundColor(), scrollPane.getBackground());
        assertEquals("menuPanel의 배경색은 DarkTheme의 배경색과 동일해야 합니다.",
            darkTheme.getBackgroundColor(), menuPanel.getBackground());
    }

    @Test
    public void testThemeChange() {
        // 테스트할 Theme 인스턴스 생성 (DarkTheme)
        Theme theme = DarkTheme.getInstance();

        // 테마 변경의 영향을 받을 UI 컴포넌트 생성
        JPanel contentPane = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        JPanel menuPanel = new JPanel();

        // 초기 배경색 설정
        contentPane.setBackground(Color.WHITE);
        scrollPane.setBackground(Color.WHITE);
        menuPanel.setBackground(Color.WHITE);

        // SideMenu 관련 객체 생성
        JPanel sideContentPane = new JPanel();
        Dimension buttonSizeInPixels = new Dimension(32, 32);
        Config uiConfig = SwingConfigSetup.createConfig();
        Color backgroundColor = Color.WHITE;

        // BitmapCache 생성 (테스트용 로더와 스케일러 사용)
        BitmapCache<SwingBitmap> bitmapCache = new BitmapCache<>(
            new FakeBitmapLoader(), new FakeBitmapScaler(), Runtime.getRuntime().maxMemory() / 8);

        SideMenu sideMenu = new SideMenu(
            sideContentPane, bitmapCache, buttonSizeInPixels, uiConfig, backgroundColor);

        // MainJFrame 생성 (Sound는 테스트용으로 null 사용)
        MainJFrame mainJFrame = new MainJFrame(uiConfig, new MockSound());

        // MenuUi 생성
        MenuUi menuUi = new MenuUi(
            null, // 파일 시스템을 사용하지 않으므로 null 전달
            System.out,
            Locale.getDefault(),
            bitmapCache,
            uiConfig,
            mainJFrame,
            new MockSound()); // 가짜 사운드 객체 사용

        // 테마 변경 메소드 호출
        theme.change(sideMenu, menuUi, contentPane, scrollPane, menuPanel);

        // 테마의 배경색과 일치하는지 확인
        Color expectedColor = theme.getBackgroundColor();
        assertEquals("ContentPane의 배경색은 테마의 배경색과 일치해야 합니다.", expectedColor, contentPane.getBackground());
        assertEquals("ScrollPane의 배경색은 테마의 배경색과 일치해야 합니다.", expectedColor, scrollPane.getBackground());
        assertEquals("MenuPanel의 배경색은 테마의 배경색과 일치해야 합니다.", expectedColor, menuPanel.getBackground());

        // SideMenu의 배경색 확인 (리플렉션 사용)
        Color sideMenuColor = getSideMenuPanelBackgroundColor(sideMenu);
        assertEquals("SideMenu의 배경색은 테마의 배경색과 일치해야 합니다.", expectedColor, sideMenuColor);
    }

    @Test
    public void testGetThemeFromConfig() {
        // uiConfig를 생성한다.
        Config uiConfig = SwingConfigSetup.createConfig();

        // 'dark' 설정을 명시적으로 false로 설정한다.
        ConfigTools.setBool(uiConfig, CFG_DARK_THEME, false);

        // 'dark' 설정 값을 가져온다.
        boolean isDark = ConfigTools.getBool(uiConfig, CFG_DARK_THEME);

        // 'dark' 설정이 false인지 확인한다.
        assertFalse("초기 설정에서 'dark' 값은 false여야 합니다.", isDark);

        // getTheme을 호출하여 테마를 가져온다.
        Theme theme = Theme.getTheme(uiConfig);

        // 테마가 BrightTheme인지 확인한다.
        assertTrue("dark 설정이 false이면 테마는 BrightTheme이어야 합니다.", theme instanceof BrightTheme);

        // uiConfig에서 dark 모드를 true로 설정한다.
        ConfigTools.setBool(uiConfig, CFG_DARK_THEME, true);

        // 설정 값이 변경되었는지 확인한다.
        isDark = ConfigTools.getBool(uiConfig, CFG_DARK_THEME);
        assertTrue("'dark' 설정을 true로 변경하면 isDark는 true여야 합니다.", isDark);

        // getTheme을 다시 호출한다.
        theme = Theme.getTheme(uiConfig);

        // 테마가 DarkTheme인지 확인한다.
        assertTrue("dark 설정이 true이면 테마는 DarkTheme이어야 합니다.", theme instanceof DarkTheme);
    }

    // BrightTheme의 색상 값 테스트
    @Test
    public void testBrightThemeColors() {
        // BrightTheme의 인스턴스를 가져온다.
        BrightTheme brightTheme = (BrightTheme) BrightTheme.getInstance();

        // 각 색상 값을 가져온다.
        Color backgroundColor = brightTheme.getBackgroundColor();
        Color graphPaperMajor = brightTheme.getGraphPaperMajor();
        Color graphPaperMinor = brightTheme.getGraphPaperMinor();
        Color waterColor = brightTheme.getWaterColor();

        // 예상되는 색상과 비교한다.
        assertEquals("BrightTheme의 배경색은 Color.WHITE이어야 합니다.", Color.WHITE, backgroundColor);
        assertEquals("BrightTheme의 graphPaperMajor 색상은 (205,212,220)이어야 합니다.",
            new Color(205, 212, 220), graphPaperMajor);
        assertEquals("BrightTheme의 graphPaperMinor 색상은 (235,243,255)이어야 합니다.",
            new Color(235, 243, 255), graphPaperMinor);
        assertEquals("BrightTheme의 waterColor는 (130,167,221)이어야 합니다.",
            new Color(130, 167, 221), waterColor);
    }

    // 테마 변경 시 Config의 값이 올바르게 변경되는지 테스트
    @Test
    public void testThemeChangeUpdatesConfig() {
        // uiConfig를 생성하고, 초기값을 설정한다.
        Config uiConfig = SwingConfigSetup.createConfig();

        // 'dark' 설정을 명시적으로 false로 설정한다.
        ConfigTools.setBool(uiConfig, CFG_DARK_THEME, false);

        // 'dark' 설정이 초기에는 false여야 한다.
        boolean isDark = ConfigTools.getBool(uiConfig, CFG_DARK_THEME);
        assertFalse("초기 설정에서 'dark' 값은 false여야 합니다.", isDark);

        // 테마를 Dark로 변경한다.
        ConfigTools.setBool(uiConfig, CFG_DARK_THEME, true);

        // uiConfig의 "dark" 값이 true로 변경되었는지 확인한다.
        isDark = ConfigTools.getBool(uiConfig, CFG_DARK_THEME);
        assertTrue("테마를 Dark로 변경하면 uiConfig의 'dark' 값이 true여야 합니다.", isDark);

        // 다시 테마를 Bright로 변경한다.
        ConfigTools.setBool(uiConfig, CFG_DARK_THEME, false);

        // uiConfig의 "dark" 값이 false로 변경되었는지 확인한다.
        isDark = ConfigTools.getBool(uiConfig, CFG_DARK_THEME);
        assertFalse("테마를 Bright로 변경하면 uiConfig의 'dark' 값이 false여야 합니다.", isDark);
    }

    // Theme 클래스의 추상 메소드 구현 확인 (추가적인 메소드 테스트)
    @Test
    public void testThemeAbstractMethods() {
        // DarkTheme의 인스턴스를 가져온다.
        Theme darkTheme = DarkTheme.getInstance();

        // setSideMenuColor 메소드가 올바르게 동작하는지 확인한다.
        // 필요한 객체들을 생성한다.
        Container contentPane = new JPanel();
        Dimension buttonSizeInPixels = new Dimension(32, 32);
        Config uiConfig = SwingConfigSetup.createConfig();
        Color backgroundColor = Color.WHITE;

        // BitmapCache 생성
        FakeBitmapLoader loader = new FakeBitmapLoader();
        FakeBitmapScaler scaler = new FakeBitmapScaler();
        BitmapCache<SwingBitmap> bitmapCache = new BitmapCache<SwingBitmap>(
            loader, scaler, Runtime.getRuntime().maxMemory() / 8 );

        // SideMenu를 생성한다.
        SideMenu sideMenu = new SideMenu(contentPane, bitmapCache, buttonSizeInPixels, uiConfig, backgroundColor);

        // sideMenu의 배경색을 초기화한다.
        sideMenu.setPanelBackground(Color.RED);

        // darkTheme을 사용하여 sideMenu의 색상을 설정한다.
        darkTheme.setSideMenuColor(sideMenu);

        // sideMenu의 배경색을 가져오기 위해 리플렉션을 사용한다.
        Color actualColor = getSideMenuPanelBackgroundColor(sideMenu);

        // sideMenu의 배경색이 darkTheme의 배경색과 동일한지 확인한다.
        assertEquals("DarkTheme의 setSideMenuColor는 올바르게 동작해야 합니다.",
            darkTheme.getBackgroundColor(), actualColor);

        // BrightTheme에서도 동일하게 테스트한다.
        Theme brightTheme = BrightTheme.getInstance();
        brightTheme.setSideMenuColor(sideMenu);
        actualColor = getSideMenuPanelBackgroundColor(sideMenu);
        assertEquals("BrightTheme의 setSideMenuColor는 올바르게 동작해야 합니다.",
            brightTheme.getBackgroundColor(), actualColor);
    }

    // ConfigTools의 getBool 메소드를 사용하여 Theme을 적절히 가져오는 테스트
    @Test
    public void testGetThemeUsingConfigTools() {
        // uiConfig를 생성하고, 초기값을 설정한다.
        Config uiConfig = SwingConfigSetup.createConfig();

        // 'dark' 설정을 명시적으로 false로 설정한다.
        ConfigTools.setBool(uiConfig, CFG_DARK_THEME, false);

        // 'dark' 설정을 확인한다.
        boolean isDark = ConfigTools.getBool(uiConfig, CFG_DARK_THEME);
        assertFalse("초기 설정에서 'dark' 값은 false여야 합니다.", isDark);

        // 현재 테마를 가져온다.
        Theme theme = Theme.getTheme(uiConfig);
        assertTrue("초기 설정에서 테마는 BrightTheme이어야 합니다.", theme instanceof BrightTheme);

        // 'dark' 설정을 true로 변경한다.
        ConfigTools.setBool(uiConfig, CFG_DARK_THEME, true);

        // 변경된 설정으로 테마를 다시 가져온다.
        theme = Theme.getTheme(uiConfig);
        isDark = ConfigTools.getBool(uiConfig, CFG_DARK_THEME);
        assertTrue("'dark' 설정을 true로 변경하면 isDark는 true여야 합니다.", isDark);
        assertTrue("'dark' 설정을 true로 변경하면 테마는 DarkTheme이어야 합니다.", theme instanceof DarkTheme);

        // 'dark' 설정을 false로 변경한다.
        ConfigTools.setBool(uiConfig, CFG_DARK_THEME, false);

        // 변경된 설정으로 테마를 다시 가져온다.
        theme = Theme.getTheme(uiConfig);
        isDark = ConfigTools.getBool(uiConfig, CFG_DARK_THEME);
        assertFalse("'dark' 설정을 false로 변경하면 isDark는 false여야 합니다.", isDark);
        assertTrue("'dark' 설정을 false로 변경하면 테마는 BrightTheme이어야 합니다.", theme instanceof BrightTheme);
    }

    // FakeBitmapLoader 클래스 정의
    private static class FakeBitmapLoader implements BitmapLoader<SwingBitmap> {
        @Override
        public SwingBitmap load(String fileName, int tileSize) {
            BufferedImage img = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            return new SwingBitmap(fileName, img);
        }

        @Override
        public int sizeFor(int tileSize) {
            return tileSize;
        }
    }

    // FakeBitmapScaler 클래스 정의
    private static class FakeBitmapScaler implements BitmapScaler<SwingBitmap> {
        @Override
        public SwingBitmap scale(SwingBitmap originalBitmap, double scale) {
            // 테스트를 위해 원본 비트맵을 그대로 반환합니다.
            return originalBitmap;
        }
    }

    // Sound의 가짜 구현체
    private static class MockSound implements Sound {
        @Override
        public void setMusic( String music )
        {

        }

        @Override
        public void playSound( String soundEffect )
        {

        }

        @Override
        public void mute( boolean muted )
        {

        }

        @Override
        public void dispose() {
            // 아무 작업도 하지 않음
        }
    }
}
