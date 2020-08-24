package com.formkiq.graalvm.processors;

import com.formkiq.graalvm.annotations.Reflectable;

@Reflectable
public class Test3 {

  /** Foo. */
  @Reflectable private String foo;

  /** Bar. */
  private String bar;

  public String getFoo() {
    return foo;
  }

  public void setFoo(final String val) {
    this.foo = val;
  }

  public String getBar() {
    return bar;
  }

  public void setBar(final String val) {
    this.bar = val;
  }

  @Reflectable
  public void foo(final String foobar) {
    // empty
  }
}
