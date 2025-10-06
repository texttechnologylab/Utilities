import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphFactory;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraphFactory;
import com.tinkerpop.blueprints.util.VertexHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.utilities.helper.utils.YedFileWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class YedTest {

    @Test
    public void test() throws IOException {

        Graph graph = new TinkerGraph();

        Vertex a = graph.addVertex(null);
        Vertex b = graph.addVertex(null);
        a.setProperty("label", "alpha");
        b.setProperty("label", "beta");

        Edge e = graph.addEdge(null, a, b, "knows");

        YedFileWriter yed = new YedFileWriter(graph);
        yed.outputGraph(new FileOutputStream(new File("/tmp/test.graphml")));

    }

}
