package org.json;

/**
 * Configuration object for the JSON parser. The configuration is immutable.
 */
public class JSONParserConfiguration extends ParserConfiguration {

  /**
   * We can override the default maximum nesting depth if needed.
   */
  public static final int DEFAULT_MAXIMUM_NESTING_DEPTH = ParserConfiguration.DEFAULT_MAXIMUM_NESTING_DEPTH;

  /**
   * Configuration with the default values.
   */
  public JSONParserConfiguration() {
    this.maxNestingDepth = DEFAULT_MAXIMUM_NESTING_DEPTH;
  }

  public JSONParserConfiguration(int maxNestingDepth) {
    this.maxNestingDepth = maxNestingDepth;
  }

  @Override
  protected JSONParserConfiguration clone() {
    return new JSONParserConfiguration(DEFAULT_MAXIMUM_NESTING_DEPTH);
  }

}
