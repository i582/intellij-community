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
      int y = getX();
      return y;
    } else {
      int x = getX();
      return x;
    }
  }
}