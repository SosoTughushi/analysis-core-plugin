package hudson.plugins.analysis.ast;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Depicts the MethodOrClassAst. If the warning is on method-level the {@link MethodAst#chooseArea()} would be called,
 * otherwise if it is on class-level the {@link ClassAst#chooseArea()}.
 *
 * @author Christian M�stl
 */
public class MethodOrClassAst extends Ast {
    private static final int[] nonMethodTypes
            = {TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF, TokenTypes.ENUM_DEF, TokenTypes.ANNOTATION_DEF};

    /**
     * Creates a new instance of {@link MethodOrClassAst}.
     *
     * @param fileName   the name of the Java file
     * @param lineNumber the line number that contains the warning
     */
    public MethodOrClassAst(final String fileName, final int lineNumber) {
        super(fileName, lineNumber);
    }

    @Override
    public List<DetailAST> chooseArea() {
        if (getElementsInSameLine().size() != 0 && isLevelOfMethod(getElementsInSameLine().get(0))) {
            return new MethodAst(getFileName(), getLineNumber()).chooseArea();
        }
        else {
            return new ClassAst(getFileName(), getLineNumber()).chooseArea();
        }
    }

    private boolean isLevelOfMethod(final DetailAST element) {
        if (element != null) {
            if (element.getType() == TokenTypes.METHOD_DEF || element.getType() == TokenTypes.CTOR_DEF) {
                return true;
            }
            else if (ArrayUtils.contains(nonMethodTypes, element.getType())) {
                return false;
            }
            else {
                return isLevelOfMethod(element.getParent());
            }
        }
        return false;
    }
}