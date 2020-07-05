import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfiniteQuest {
    int questNr; // no meaning, but good for information, which quest has been completed
    Map<Long, Long> pidToAmountMap = new HashMap<>(); // product id and how many I need for the infinitequest. Typically exactly 2 entries of the list
    List<String> names = new ArrayList<>(); // for readability purposes, since pid doesn't tell a human much about which product it is

    @Override
    public String toString() {
        return "InfiniteQuest{" +
                "questNr=" + questNr +
                ", pidToAmountMap=" + pidToAmountMap +
                ", names=" + names +
                '}';
    }
}
