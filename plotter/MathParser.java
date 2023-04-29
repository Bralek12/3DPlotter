import org.jzy3d.plot3d.builder.Mapper;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.jzy3d.maths.BoundingBox3d;


public abstract class MathParser {
    private final Expression expression;
    private final Argument x;
    private final Argument y;

    public MathParser(String input) {
        this.expression = new Expression(input);
        this.x = new Argument("x");
        this.y = new Argument("y");
        this.expression.addArguments(this.x, this.y);
    }

    public double calculate(double xVal, double yVal) {
        this.x.setArgumentValue(xVal);
        this.y.setArgumentValue(yVal);
        return this.expression.calculate();
    }

    public static Mapper parseExpression(String userInput, Argument x, Argument y) {
        // Create MXParser expression object
        Expression expression = new Expression(userInput);

        // Add arguments to expression
        expression.addArguments(x, y);

        // Create Mapper object
        return new Mapper() {
            @Override
            public double f(double xVal, double yVal) {
                // Set argument values
                x.setArgumentValue(xVal);
                y.setArgumentValue(yVal);

                // Evaluate expression
                return expression.calculate();
            }
        };
    }

}
