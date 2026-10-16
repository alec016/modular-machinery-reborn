package es.degrassi.mmr.common.util;

import es.degrassi.mmr.ModularMachineryReborn;
import es.degrassi.mmr.common.data.MMRConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class MMRLogger {
  private MMRLogger() {}

  public static final Logger INSTANCE = ModularMachineryReborn.LOGGER;
  public static final String NAME = "Modular Machinery Reborn";
  private static boolean shouldReset = false;

  public static void init() {
    final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    final Configuration config = ctx.getConfiguration();

    PatternLayout logPattern = PatternLayout.newBuilder()
      .withPattern("[%d{HH:mm:ss.SSS}][%level]: %msg%n%throwable")
      .build();

    TriggeringPolicy policy = new TriggeringPolicy() {
      @Override
      public void initialize(RollingFileManager manager) {
        // Required to create an instance
      }

      @Override
      public boolean isTriggeringEvent(LogEvent logEvent) {
        if(shouldReset) {
          shouldReset = false;
          return true;
        }
        return false;
      }
    };

    RollingFileAppender cmAppender = RollingFileAppender.newBuilder()
      .withFileName("logs/modular_machinery_reborn/mmr.log")
      .withAppend(false)
      .withFilePattern("logs/modular_machinery_reborn/mmr-%i.log.gz")
      .withPolicy(policy)
      .setName(NAME)
      .setImmediateFlush(true)
      .setIgnoreExceptions(false)
      .setConfiguration(config)
      .setLayout(logPattern)
      .build();

    cmAppender.start();

    config.addAppender(cmAppender);

    LoggerConfig loggerConfig = LoggerConfig.newBuilder()
      .withAdditivity(false)
      .withLevel(Level.ALL)
      .withLoggerName(NAME)
      .withIncludeLocation("true")
      .withRefs(new AppenderRef[0])
      .withProperties(null)
      .withConfig(config)
      .withtFilter(null)
      .build();

    loggerConfig.addAppender(cmAppender, MMRConfig.get().debugLevel.get().getLevel(), null);

    Appender debug = config.getAppender("DebugFile");
    if(debug != null)
      loggerConfig.addAppender(debug, Level.WARN, null);

    Appender file = config.getAppender("File");
    if(file != null)
      loggerConfig.addAppender(file, Level.WARN, null);

    Appender console = config.getAppender("Console");
    if(console != null)
      loggerConfig.addAppender(console, Level.WARN, null);

    Appender serverGuiConsole = config.getAppender("ServerGuiConsole");
    if(serverGuiConsole != null)
      loggerConfig.addAppender(serverGuiConsole, Level.WARN, null);

    config.addLogger(NAME, loggerConfig);
    ctx.updateLoggers();
  }

  public static void reset() {
    shouldReset = true;
  }

  public static void setDebugLevel(Level level) {
    final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    final Configuration config = ctx.getConfiguration();

    final LoggerConfig cmConfig = config.getLoggers().get(NAME);
    if(cmConfig == null)
      throw new IllegalStateException(NAME + " logger not present!");

    final Appender cmAppender = cmConfig.getAppenders().get(NAME);
    if(cmAppender == null)
      throw new IllegalStateException(NAME + " appender not present");

    cmConfig.removeAppender(NAME);
    cmConfig.addAppender(cmAppender, level, null);
  }
}
