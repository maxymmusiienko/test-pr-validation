package net.broscorp.gcimpl;

import java.util.Map;

import lombok.Getter;

@Getter
public class HeapInfo {

  private Map<String, ApplicationBean> beans;
  private int dmkladkla = 5;
  public HeapInfo(Map<String, ApplicationBean> beans) {
    this.beans = beans;
    System.out.println("Changed wrong file.");
  }
}
