package jp.co.misumi.misumiecapp.widget;

/**
 * Interface for UI widgets (particularly, checkable widgets) whose checked state can be changed programmatically, without triggering the {@code OnCheckedChangeListener}
 * 
 */
public interface ProgrammaticallyCheckable {
  void setCheckedEx(boolean checked);
}
