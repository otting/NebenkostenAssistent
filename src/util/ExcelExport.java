package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;

import javax.naming.directory.InvalidAttributesException;

import db.Garage;
import db.House;
import gui.ErrorHandle;

public class ExcelExport {

    public final static String PATH = System.getProperty("user.dir") + "\\Vorlage.xls";

    public static void test() throws FileNotFoundException, IOException {

    }

    public static void exportBills(int year, House house, HouseBill houseBill, LinkedList<TenantBill> bills)
	    throws IOException, InterruptedException, InvalidAttributesException {
	String exlName = house.toString() + year + ".xls";
	ExcelHandle exl = null;
	exl = loadTemplate(exlName);

	exportHouseInfo(exl, houseBill, year);

	for (TenantBill tb : bills) {
	    exportBill(exl, tb, year);
	}
	// remove unneeded sheet cloned in exportBill
	exl.selectLastSheet().removeSelectedSheet();
	StatSchieber.copyAll(exl);
	exl.save();
    }

    private static ExcelHandle loadTemplate(String newName) throws IOException {
	try {
	    return ExcelHandle.copyExcel(PATH, newName);
	} catch (IOException e) {
	    e.printStackTrace();
	    if (ErrorHandle.askYesNo("Could not open template at " + PATH + "\nRetry?")) {
		loadTemplate(newName);
	    } else
		throw new IOException("Template could not be opened");
	}
	return null;
    }

    /**
     * exports the values of a Bill into the excel sheet
     * 
     * @param exl
     * @param b
     * @param year
     * @throws InterruptedException
     * @throws Exception
     */
    private static void exportBill(ExcelHandle exl, TenantBill b, int year) throws InterruptedException {
	exl.selectLastSheet();
	exl.cloneLastSheet();
	exl.renameSelectedSheet(b.getTenant().getName());
	exl.replace("*NAME*", b.getTenant().getName());
	exl.replace("*WOHNUNG*", b.getFlat().toString());
	exl.replace("*QM*", b.getSquareMeter());
	exl.replace("*WANTEIL*", b.getFlat().getShare());
	exl.replace("*PERSO_ZAHL*", b.getPersonCount());
	exl.replace("*JAHR*", year);
	exl.replace("*WASSER_KALT*", b.getColdWater());
	exl.replace("*WASSER_WARM*", b.getHotWater());
	exl.replace("*GEZAHLT*", b.getBalance());
	exl.replace("*HEIZUNG*", b.getHeater());
	exl.replace("*START*", b.getStart());
	exl.replace("*ENDE*", b.getEnd());
	exl.replace("*GRUNDWOHNUNG*", b.getFlat().getGrundsteuer(year));

	exl.replace("*MIETE*", b.getTenant().getRent(year));
	Calendar cal = Calendar.getInstance();
	cal.set(year, 12, 31);
	exl.replace("*AKTPERSO*", b.getTenant().getPersonCount().getLast());
	cal = Calendar.getInstance();
	cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
	exl.replace("*NACHZAHLDATUM*", cal.getTime());

	// Leerstand
	if (b.getTenant().getName().equals("Leerstand")) {
	    // Kabel Extra Kosten
	    exl.replace("*BEREITSTELLUNG*", 0);
	    exl.replace("*HAUSVERTEILUNG*", 0);

	    // Garage
	    exl.replace("*GARAGECOUNT*", 0);
	    exl.replace("*GARAGE*", 0);
	    exl.replace("*NEXTGARAGERENT*", 0);

	    // Stellplatz
	    exl.replace("*STELLP*", 0);
	    exl.replace("*STELLPANZ*", 0);
	    exl.replace("*NEXTSTELLPRENT*", 0);
	    return;
	}
	// Kabel Extra Kosten
	exl.replace("*BEREITSTELLUNG*", b.getTenant().getCableProvidingCost(year));
	exl.replace("*HAUSVERTEILUNG*", b.getTenant().getHouseCableSupplyCost(year));

	// Garage
	exl.replace("*GARAGECOUNT*", b.getTenant().getGarageUsage(year));
	exl.replace("*GARAGE*", b.getTenant().getGarageRent(year));
	exl.replace("*NEXTGARAGERENT*", b.getTenant().getFutureGarageRent(year));

	// Stellplatz
	exl.replace("*STELLP*", b.getTenant().getStellPRent(year));
	exl.replace("*STELLPANZ*", b.getTenant().getStellPUsage(year));
	exl.replace("*NEXTSTELLPRENT*", b.getTenant().getFutureStellPRent(year));
    }

    private static void exportHouseInfo(ExcelHandle exl, HouseBill b, int year)
	    throws InvalidAttributesException, InterruptedException {
	exl.selectSheet("Haus");
	exl.renameSelectedSheet(b.getHouse().toString());
	exl.replace("*HAUS*", b.getHouse().toString());
	exl.replace("*QM*", b.getSquareMeter());
	exl.replace("*PERSO_ZAHL*", b.getPersonCount());
	exl.replace("*FLATCOUNT*", b.getFlatCount());
	exl.replace("*ANTEIL*", b.getShare());
	exl.replace("*WARMWASSER*", b.getHotWater());
	exl.replace("*WASSER*", b.getColdWater());
	exl.replace("*GRUND*", b.getBaseTax());
	exl.replace("*GARTEN*", b.getHouse().getGardenCost(year));
	exl.replace("*GARAGE*", Garage.getNebenkosten(year));
	exl.replace("*GARAGECOUNT*", Garage.getCount());

	try {
	    exl.replace("*WWKOSTEN*", b.getHotWaterCost());
	    exl.replace("*MUELL*", b.getTrashCost());
	    exl.replace("*WASSERKOSTEN*", b.getWaterCost());
	    exl.replace("*KANAL*", b.getWasteWaterCost());
	    exl.replace("*INTERNET*", b.getCableCost());
	    exl.replace("*REGEN*", b.getRainCost());
	    exl.replace("*VERSICHER*", b.getEnsuranceCost());
	    exl.replace("*ALGSTROM*", b.getCommonElectricCost());

	} catch (InvalidAttributesException e) {
	    ErrorHandle.popUp(e.getExplanation());
	    e.printStackTrace();
	    throw new InvalidAttributesException("Important values are not set");
	}
    }
}
