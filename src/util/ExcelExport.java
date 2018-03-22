package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;

import javax.naming.directory.InvalidAttributesException;

import db.Garage;
import db.House;
import db.Tenant;
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
     * @param bill
     * @param year
     * @throws InterruptedException
     * @throws Exception
     */
    private static void exportBill(ExcelHandle exl, TenantBill bill, int year) throws InterruptedException {
	exl.selectLastSheet();
	exl.cloneLastSheet();
	Tenant tenant = bill.getTenant();
	try {
	    exl.renameSelectedSheet(tenant.getName());
	} catch (Exception e) {
	    try {
		exl.renameSelectedSheet(tenant.getName() + (1));
	    } catch (Exception e2) {
		throw new InterruptedException("Konnte Worksheet nicht umbennen für " + tenant.getName());
	    }
	}

	exl.replace("*NAME*", tenant.getName());
	exl.replace("*WOHNUNG*", bill.getFlat().toString());
	exl.replace("*QM*", bill.getSquareMeter());
	exl.replace("*WANTEIL*", bill.getFlat().getShare());
	exl.replace("*PERSO_ZAHL*", bill.getPersonCount());
	exl.replace("*JAHR*", year);
	exl.replace("*WASSER_KALT*", bill.getColdWater());
	exl.replace("*WASSER_WARM*", bill.getHotWater());
	exl.replace("*GEZAHLT*", bill.getBalance());
	exl.replace("*HEIZUNG*", bill.getHeater());
	exl.replace("*PRE_Heizung*", tenant.getPrepayedHeaterCost(year));
	exl.replace("*START*", bill.getStart());
	exl.replace("*ENDE*", bill.getEnd());
	exl.replace("*GRUNDWOHNUNG*", bill.getFlat().getGrundsteuer(year));
	exl.replace("*MODBESCHREIBUNG*", tenant.getModernisierung(year).description);
	exl.replace("*MODERN*", tenant.getModernisierung(year).value);
	exl.replace("*SONSTBESCHREIBUNG*", tenant.getSonstige(year).description);
	exl.replace("*SONSTIGE*", tenant.getSonstige(year).value);

	exl.replace("*MIETE*", tenant.getRent(year));
	Calendar cal = Calendar.getInstance();
	cal.set(year, 12, 31);
	exl.replace("*AKTPERSO*", tenant.getPersonCount().getLast());
	cal = Calendar.getInstance();
	cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
	exl.replace("*NACHZAHLDATUM*", cal.getTime());

	// Leerstand
	if (tenant.getName().equals("Leerstand")) {
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
	} else {
	    // Kabel Extra Kosten
	    exl.replace("*BEREITSTELLUNG*", tenant.getCableProvidingCost(year));
	    exl.replace("*HAUSVERTEILUNG*", tenant.getHouseCableSupplyCost(year));

	    // Garage
	    exl.replace("*GARAGECOUNT*", tenant.getGarageUsage(year));
	    exl.replace("*GARAGE*", tenant.getGarageRent(year));
	    exl.replace("*NEXTGARAGERENT*", tenant.getFutureGarageRent(year));

	    // Stellplatz
	    exl.replace("*STELLP*", tenant.getStellPRent(year));
	    exl.replace("*STELLPANZ*", tenant.getStellPUsage(year));
	    exl.replace("*NEXTSTELLPRENT*", tenant.getFutureStellPRent(year));
	}
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
