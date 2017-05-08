package com.fil.shauni.mainframe.spi;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Chiara
 */
public class CommandContext {
  @Getter @Setter
  private List<String> urls;
  
  @Getter
  private final boolean crypto;
  
  public CommandContext(boolean crypto) {
      this.crypto = crypto;
  }
}
