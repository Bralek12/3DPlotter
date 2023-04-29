import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jzy3d.bridge.awt.FrameAWT;
import org.jzy3d.chart.AWTChart;
import org.jzy3d.chart.factories.AWTChartFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.colors.ColorMapper;
import org.jzy3d.colors.colormaps.ColorMapGrayscale;
import org.jzy3d.colors.colormaps.ColorMapHotCold;
import org.jzy3d.colors.colormaps.ColorMapRainbow;
import org.jzy3d.colors.colormaps.ColorMapRedAndGreen;
import org.jzy3d.colors.colormaps.ColorMapWhiteRed;
import org.jzy3d.colors.colormaps.IColorMap;
import org.jzy3d.maths.BoundingBox3d;
import org.jzy3d.maths.Range;
import org.jzy3d.plot3d.builder.Mapper;
import org.jzy3d.plot3d.builder.SurfaceBuilder;
import org.jzy3d.plot3d.builder.concrete.OrthonormalGrid;
import org.jzy3d.plot3d.primitives.Shape;
import org.jzy3d.plot3d.rendering.canvas.CanvasAWT;
import org.jzy3d.plot3d.rendering.view.ViewportMode;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;

import org.mariuszgromada.math.mxparser.*;

public class App implements MouseListener, ActionListener, GLEventListener {

	static final float ALPHA_FACTOR = 0.75f;

	private AWTChart chart;
	// private AWTView view;
	private CanvasAWT canvas;
	private FrameAWT frame;
	private Choice mathModels = new Choice();
	private Choice colorMaps = new Choice();
	private TextField functionInput = new TextField();
	private Mapper mapper;
	private IColorMap colorMap;
	private Shape surface = null;

	// Define x and y as fields
	public Argument x = new Argument("x");
	public Argument y = new Argument("y");

	// Init expression
	public String initExpression = "x * sin(x * y)";

	public App() {
		this.mapper = getInitialMapper();

		AWTChartFactory f = new AWTChartFactory();
		this.chart = (AWTChart) f.newChart();
		this.colorMap = new ColorMapWhiteRed();

		rebuildMathematicalModel();

		this.frame = (FrameAWT) chart.open("App3d", 800, 900);
		this.frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		this.canvas = (CanvasAWT) chart.getCanvas();
		this.chart.addMouseCameraController();
		canvas.addMouseController(this);

		AddFeatures();
	}

	public void AddFeatures() {

		// Define the functionInput variable as an instance variable:
		functionInput = new TextField();
		functionInput.setBounds(100, 850, 200, 30);
		functionInput.setText("x * sin(x * y)");
		this.frame.add(functionInput);

		colorMaps.add("1. White - Red");
		colorMaps.add("2. Red - Green");
		colorMaps.add("3. Hot Cold");
		colorMaps.add("4. Grayscale");
		colorMaps.add("5. Rainbow");
		colorMaps.setBounds(300, 850, 200, 50);

		Component[] comps = this.frame.getComponents();
		comps[0].setBounds(50, 50, 760, 760);

		Button applyButton = new Button("Apply");
		applyButton.setBounds(600, 850, 80, 30);
		applyButton.addActionListener(this);

		Button resetButton = new Button("Reset");
		resetButton.setBounds(700, 850, 80, 30);
		resetButton.addActionListener(this);

		this.frame.add(functionInput);
		this.frame.add(colorMaps);
		this.frame.add(applyButton);
		this.frame.add(resetButton);
		this.frame.setLayout(null);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("Apply") == 0) {
			if (colorMaps.getSelectedItem().startsWith("1."))
				this.colorMap = new ColorMapWhiteRed();
			if (colorMaps.getSelectedItem().startsWith("2."))
				this.colorMap = new ColorMapRedAndGreen();
			if (colorMaps.getSelectedItem().startsWith("3."))
				this.colorMap = new ColorMapHotCold();
			if (colorMaps.getSelectedItem().startsWith("4."))
				this.colorMap = new ColorMapGrayscale();
			if (colorMaps.getSelectedItem().startsWith("5."))
				this.colorMap = new ColorMapRainbow();

			String userInput = functionInput.getText();
			System.out.println(userInput);
			this.mapper = MathParser.parseExpression(userInput, x, y);

			rebuildMathematicalModel();
		} else if (e.getActionCommand().compareTo("Reset") == 0) {
			this.colorMap = new ColorMapWhiteRed();
			this.mapper = MathParser.parseExpression(initExpression, x, y);
			resetView();
			rebuildMathematicalModel();
		}
	}

	private void rebuildMathematicalModel()
	{
		if(this.surface != null)
			this.chart.remove(surface);
		
		// Create a surface drawing that function
		Range range = new Range(-3, 3);
		int steps = 50;

		SurfaceBuilder builder = new SurfaceBuilder();
		this.surface = builder.orthonormal(new OrthonormalGrid(range, steps, range, steps), this.mapper);

		ColorMapper colorMapper = new ColorMapper(this.colorMap, surface.getBounds().getZmin(),
		surface.getBounds().getZmax(), new Color(1, 1, 1, ALPHA_FACTOR));
		surface.setColorMapper(colorMapper);
		surface.setFaceDisplayed(true);
		surface.setWireframeDisplayed(true);
		surface.setWireframeColor(Color.BLACK);
		surface.setBoundingBoxColor(Color.RED);

		this.chart.add(this.surface);
		this.chart.getView().updateBounds();
		}

	private void resetView(){
		BoundingBox3d bounds = new BoundingBox3d(-3, 3, -3, 3, -3, 3);
		chart.getView().setBoundManual(bounds);
	}

	private Mapper getInitialMapper() {
		mapper = MathParser.parseExpression(initExpression, x, y);		
		return mapper;
	}


	private IColorMap getInitialColorMap() {
		return new ColorMapWhiteRed();
	}

	@Override
	public void display(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'display'");
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'dispose'");
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'init'");
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'reshape'");
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public static void main(String[] args) throws Exception {
		System.out.println("Opening App3D ..");
		License.iConfirmNonCommercialUse("Petr Martinek");
		App d = new App();
	}
}
