// "Collapse if statement " "true"

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class IfStatementWithIdenticalBranches {
  int getX() {
    return 42;
  }

  void work() {
    if(true) <caret> {
      int x = getX();
      return;
    }
    int x = getX();
    return;
  }
}