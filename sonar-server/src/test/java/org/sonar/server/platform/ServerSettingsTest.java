/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.server.platform;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Test;
import org.sonar.api.config.GlobalPropertyChangeHandler;
import org.sonar.api.config.PropertyDefinitions;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

public class ServerSettingsTest {

  private static File home = getHome();

  @Test
  public void shouldLoadPropertiesFile() {
    ServerSettings settings = new ServerSettings(new PropertyDefinitions(), new BaseConfiguration(), new File("."), home, new GlobalPropertyChangeHandler[0]);

    assertThat(settings.getString("hello")).isEqualTo("world");
  }

  @Test
  public void systemPropertiesShouldOverridePropertiesFile() {
    System.setProperty("ServerSettingsTestEnv", "in_env");
    ServerSettings settings = new ServerSettings(new PropertyDefinitions(), new BaseConfiguration(), new File("."), home, new GlobalPropertyChangeHandler[0]);

    assertThat(settings.getString("ServerSettingsTestEnv")).isEqualTo("in_env");
  }

  @Test(expected = IllegalStateException.class)
  public void shouldFailIfPropertiesFileNotFound() {
    File sonarHome = new File("unknown/path");
    new ServerSettings(new PropertyDefinitions(), new BaseConfiguration(), new File("."), sonarHome, new GlobalPropertyChangeHandler[0]);
  }

  @Test
  public void activateDatabaseSettings() {
    ServerSettings settings = new ServerSettings(new PropertyDefinitions(), new BaseConfiguration(), new File("."), home, new GlobalPropertyChangeHandler[0]);

    Map<String, String> databaseProperties = ImmutableMap.of("in_db", "true");
    settings.activateDatabaseSettings(databaseProperties);

    assertThat(settings.getString("in_db")).isEqualTo("true");
  }

  @Test
  public void file_settings_override_db_settings() {
    ServerSettings settings = new ServerSettings(new PropertyDefinitions(), new BaseConfiguration(), new File("."), home, new GlobalPropertyChangeHandler[0]);
    assertThat(settings.getString("in_file")).isEqualTo("true");

    Map<String, String> databaseProperties = ImmutableMap.of("in_file", "false");
    settings.activateDatabaseSettings(databaseProperties);

    assertThat(settings.getString("in_file")).isEqualTo("true");
  }

  private static File getHome() {
    try {
      return new File(ServerSettingsTest.class.getResource("/org/sonar/server/platform/ServerSettingsTest/").toURI());
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}
