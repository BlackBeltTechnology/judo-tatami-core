package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.junit.jupiter.api.Test;

@Data
class TreeNode {

    final String item;
    final List<TreeNode> nodeList;
}

public class RecursiveTemplateTest {

    @Test
    public void treeTemplateTest() throws Exception {
        Handlebars handlebars = new Handlebars();
        handlebars.setInfiniteLoops(true);
        handlebars.prettyPrint(true);

        Template template = handlebars.compile("parent");

        TreeNode tree = new TreeNode("root",
            Arrays.asList(new TreeNode("sub child",
                Arrays.asList(new TreeNode("sub sub child 1",
                    Arrays.asList(new TreeNode("sub sub sub child 1", null))),
                    new TreeNode("sub sub child 2",
                        Arrays.asList(new TreeNode("sub sub sub child 2", null)))))));

        String result = template.apply(tree);

        System.out.println(result);

    }
}
