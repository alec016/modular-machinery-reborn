package es.degrassi.mmr.common.util;


import lombok.Getter;
import org.apache.logging.log4j.Level;

@Getter
public enum LoggingLevel {
  FATAL(Level.FATAL),
  ERROR(Level.ERROR),
  WARN(Level.WARN),
  INFO(Level.INFO),
  DEBUG(Level.DEBUG),
  ALL(Level.ALL);

  private final Level level;
  LoggingLevel(Level level) {
    this.level = level;
  }

}
