/*
 * MIT License
 *
 * Copyright (c) 2020 Àngel Mariages
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package org.angelmariages.rodalieswidget.basic;

import android.graphics.Point;
import android.graphics.Rect;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.Until;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class Basic {
    private static final String APP_PACKAGE_NAME = "org.angelmariages.rodalieswidget";
    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice mDevice;

    @Before
    public void startMainActivityFromHomeScreen() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        mDevice.pressHome();

        final String launcherPackage = mDevice.getLauncherPackageName();
        assertNotNull(launcherPackage);
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        Point screenSize = new Point(mDevice.getDisplayWidth(), mDevice.getDisplayHeight());
        Point screenCenter = new Point(screenSize.x / 2, screenSize.y / 2);

        mDevice.swipe(new Point[]{screenCenter, screenCenter}, 150);

        mDevice.findObject(By.text("Widgets")).click();
        mDevice.waitForIdle();

        int halfHeight = screenSize.y / 2;

        UiObject2 widget = findWidget(screenSize.y);

        while (widget == null) {
            mDevice.swipe(screenCenter.x, halfHeight, screenCenter.x, 0, 10);
            mDevice.waitForIdle();

            widget = findWidget(screenSize.y);
        }

        Rect visibleBounds = widget.getVisibleBounds();
        Point dragWidgetPoint = new Point(visibleBounds.left + 150, visibleBounds.bottom + 150);
        Point destination = new Point(dragWidgetPoint.x + 250, dragWidgetPoint.y - 250);

        mDevice.swipe(new Point[]{dragWidgetPoint, dragWidgetPoint, destination}, 50);
        mDevice.waitForIdle();

        mDevice.wait(Until.hasObject(By.text("The origin or the destination are not set")), LAUNCH_TIMEOUT);
    }

    @After
    public void deleteWidget() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        mDevice.pressHome();

        final String launcherPackage = mDevice.getLauncherPackageName();
        assertNotNull(launcherPackage);
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        UiObject2 update = mDevice.findObject(By.res(APP_PACKAGE_NAME, "originTextView"));

        assertNotNull(update);

        Point screenSize = new Point(mDevice.getDisplayWidth(), mDevice.getDisplayHeight());

        mDevice.drag(update.getVisibleCenter().x, update.getVisibleCenter().y, screenSize.x / 2, 100, 50);
    }

    @Test
    public void widget_shouldShowAllControls_whenAdded() {
        UiObject2 updateButton = mDevice.findObject(By.res(APP_PACKAGE_NAME, "updateButton"));
        assertNotNull(updateButton);
    }

    @Test
    public void widget_shouldShowSelectedStationsForLocality_whenLocalitySelected() {
        UiObject2 update = mDevice.findObject(By.res(APP_PACKAGE_NAME, "originTextView"));

        update.click();

        mDevice.wait(Until.hasObject(By.res(APP_PACKAGE_NAME, "coreListView")), LAUNCH_TIMEOUT);

        UiObject2 barcelona = mDevice.findObject(By.text("Barcelona"));

        barcelona.click();
        mDevice.waitForIdle();

        UiObject2 stationListView = mDevice.findObject(By.res(APP_PACKAGE_NAME, "stationListView"));

        assertNotNull(stationListView);

        UiObject2 alcover = mDevice.findObject(By.text("Alcover"));

        assertNotNull(alcover);
    }

    private UiObject2 findWidget(int screenHeight) {
        UiObject2 widget = mDevice.findObject(By.text("Rodalies Widget"));

        if (widget != null) {
            Rect visibleBounds = widget.getVisibleBounds();
            int quarterOfScreenHeight = screenHeight / 4;

            if ((screenHeight - visibleBounds.bottom) > quarterOfScreenHeight) {
                return widget;
            }
        }

        return null;
    }
}
