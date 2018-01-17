package db;

public interface DbNames {

    /**
     * names for people count
     */
    String PEOPLE_COUNT = "Personenzahl";
    /**
     * names for people count
     */
    String PEOPLE_COUNT_DATE = "Datum";
    /**
     * names for people count
     */
    String PEOPLE_COUNT_TABLE = "Personenzahlen";
    /**
     * names for people count
     */
    String PEOPLE_COUNT_TENANT_ID = "ID_Mieter";

    /**
     * Messbetrag table
     */
    String MESSBETRAG_TABLE = "Messbetrag", MESSBETRAG_SQUAREMETER = "Quadratmeter", MESSBETRAG_VALUE = "Messbetrag";
    String MESSBETRAG_YEAR = "Jahr";

    /**
     * Hebesatz
     */
    String HEBESATZ_TABLE = "Hebesatz", HEBESATZ_YEAR = "Jahr", HEBESATZ_VALUE = "Hebesatz";

    /**
     * names for meter
     */
    String METER_DESCRIPTION_TABLE = "Zählerarten";
    String METER_DESCRIPTION_ID = "ID_Zählerarten";
    String METER_DESCRIPTION_NAME = "Bezeichnung";
    String METER_DESCRIPTION_TAG = "Kurz";
    /**
     * names for meter
     */
    String METER_ID = "ZählerNummer";
    String METER_FLAT = "ID_Wohnung";
    String METER_KIND = "Zählerart";
    /**
     * names for meter
     */
    String METER_MAINCOUNTER = "IstHauptzaehler";
    /**
     * names for meter
     */
    String METER_TABLE = "Zähler";
    /**
     * names for meter
     */
    String METER_TABLE_ID = "ZählerNummer";
    /**
     * names for meter
     */
    String METER_VALUES_DATE = "Datum";
    /**
     * names for meter
     */
    String METER_VALUES_METER_ID = "Zählernummer";
    String METER_VALUES_ID = "ID_Zählerwert";
    /**
     * names for meter
     */
    String METER_VALUES_TABLE = "Zählerwerte";
    /**
     * names for meter
     */
    String METER_VALUES_VALUE = "Wert";
    /**
     * meter descriptions
     */
    String ELECTRIC = "Strom";
    /**
     * meter descriptions
     */
    String HEATER = "Heizung";
    /**
     * meter descriptions
     */
    String WATER_COLD = "Kaltwasser";
    /**
     * meter descriptions
     */
    String WATER_HOT = "Warmwasser";
    /**
     * names for tenants
     */
    String TENANT_FLAT = "ID_Wohnung";
    /**
     * names for tenants
     */
    String TENANT_MOVE_IN = "Einzug";
    /**
     * names for tenants
     */
    String TENANT_MOVE_OUT = "Auszug";
    /**
     * names for tenants
     */
    String TENANT_NAME = "Name";
    /**
     * names for tenants
     */
    String TENANT_TABLE = "Mieter";
    /**
     * names for flats
     */
    String TENANT_ID = "ID_Mieter";

    String FLAT_DESCRIPTION = "Bezeichnung";
    /**
     * names for flats
     */

    String FLAT_HOUSE = "ID_Haus";
    /**
     * names for flats
     */

    String FLAT_SHARE = "Anteil";
    /**
     * names for flats
     */

    String FLAT_SQUAREMETER = "Quadratmeter";
    /**
     * names for flats
     */

    String FLAT_TABLE = "Wohnungen";
    /**
     * names for houses
     */

    String HOUSE_DESCRIPTION = "Bezeichnung";
    /**
     * names for houses
     */

    String HOUSE_NUMBER = "Hausnummer";
    /**
     * names for houses
     */

    String HOUSE_SHARE = "Anteile";
    /**
     * names for houses
     */

    String HOUSE_STREET = "Strasse";
    /**
     * names for houses
     */

    String HOUSE_TABLE = "Häuser";
    /**
     * names for houses
     */

    String HOUSE_ZIP = "Postleitzahl";
    /**
     * names for trash
     */
    String TRASH_COST = "Kosten";
    /**
     * names for trash
     */
    String TRASH_HOUSE = "ID_Haus";
    /**
     * names for trash
     */
    String TRASH_KIND = "Muellart";
    /**
     * names for trash
     */
    String TRASH_TABLE = "Abfallkosten";
    /**
     * names for trash
     */
    String TRASH_YEAR = "Abrechnungsjahr";
    String TRASH_ID = "ID_Abfall";
    /**
     * names for rain cost
     */
    String RAIN_COST = "Betrag";
    /**
     * names for rain cost
     */
    String RAIN_HOUSE = "Haus_ID";
    /**
     * names for rain cost
     */
    String RAIN_TABLE = "Regenwasser";
    /**
     * names for rain cost
     */
    String RAIN_YEAR = "Abrechnungsjahr";
    String RAIN_ID = "ID_Abwasser";
    /**
     * names for wastewater
     */
    String WASTEW_COST = "Betrag";
    /**
     * names for wastewater
     */
    String WASTEW_HOUSE = "ID_Haus";
    /**
     * names for wastewater
     */
    String WASTEW_TABLE = "Kanalgebuehr";
    /**
     * names for wastewater
     */
    String WASTEW_YEAR = "Abrechnungsjahr";
    String WASTEW_ID = "ID_Kanalgebuehr";
    /**
     * names for water cost
     */

    String WATER_COST = "Betrag";
    /**
     * names for water cost
     */

    String WATER_HOUSE = "ID_Haus";
    /**
     * names for water cost
     */

    String WATER_TABLE = "Wasserkosten";
    /**
     * names for water cost
     */

    String WATER_YEAR = "Abrechnungsjahr";
    String WATER_ID = "ID_Wasserkosten";
    /**
     * names for cable cost
     */
    String CABLE_COST = "Kosten";
    String CABLE_HOUSE = "Haus";
    /**
     * names for cable cost
     */
    String CABLE_TABLE = "KabelKosten";
    /**
     * names for cable cost
     */
    String CABLE_YEAR = "Abrechnungsjahr";
    String CABLE_ID = "ID_KabelanschlussKosten";
    /**
     * Kabel Bereitstellungsgeuehr
     */
    String PROVIDINGFEE_TABLE = "BereitstellungKabel";
    String PROVIDINGFEE_ID = "ID_KabelBereitstellung";
    String PROVIDINGFEE_TENANT = "ID_Mieter";
    String PROVIDINGFEE_YEAR = "Jahr";
    String PROVIDINGFEE_VALUE = "Betrag";

    String HOUSESUPPLY_TABLE = "Hausverteilungsanlage", HOUSESUPPLY_ID = "ID_Hausverteilungsanlage";
    String HOUSESUPPLY_TENANT = "ID_Mieter";
    String HOUSESUPPLY_YEAR = "Jahr";
    String HOUSESUPPLY_VALUE = "Betrag";

    /**
     * names for hot water
     */
    String HOTWATER_COST = "Betrag";
    /**
     * names for hot water
     */
    String HOTWATER_HOUSE = "ID_Haus";
    /**
     * names for hot water
     */
    String HOTWATER_TABLE = "Warmwasser";
    /**
     * names for hot water
     */
    String HOTWATER_YEAR = "Abrechnungsjahr";
    String HOTWATER_ID = "ID_Warmwasser";
    /**
     * ensurance names
     */
    String INSURANCE_ID = "ID_Versicherungskosten";
    String INSURANCE_COST = "Betrag";
    /**
     * ensurance names
     */
    String INSURANCE_HOUSE = "Haus";
    /**
     * ensurance names
     */
    String INSURANCE_TABLE = "Versicherung";
    /**
     * ensurance names
     */
    String INSURANCE_TYPE = "Bezeichnung";
    /**
     * ensurance names
     */
    String INSURANCE_YEAR = "Abrechnungsjahr";
    /**
     * Common Electric names
     */
    String COMMON_COST = "Betrag";
    /**
     * Common Electric names
     */
    String COMMON_HOUSE = "ID_Haus";
    /**
     * Common Electric names
     */
    String COMMON_TABLE = "Allgemeinstrom";
    String COMMON_ID = "ID_Allgemeinstrom";
    /**
     * Common Electric names
     */
    String COMMON_YEAR = "Abrechnungsjahr";
    /**
     * Total shares
     */
    String TOTALSHARE_SHARE = "Summe Anteile";
    /**
     * Total shares
     */
    String TOTALSHARE_TABLE = "GesamteAnteile";
    /**
     * Total shares
     */
    String TOTALSHARE_YEAR = "WirksamAb";
    /**
     * Tenant Money
     */
    String TENANT_MONEY_DATE = "Datum";
    /**
     * Tenant Money
     */
    String TENANT_MONEY_MONEY = "Einzahlung";
    /**
     * Tenant Money
     */
    String TENANT_MONEY_TABLE = "Mieterfinanzen";
    /**
     * Tenant Money
     */
    String TENANT_MONEY_TENANT = "Mieter";
    /**
     * Rent
     */
    String RENT_COST = "Mietbetrag";
    /**
     * Rent
     */
    String RENT_TABLE = "Miete";
    /**
     * Rent
     */
    String RENT_TENANT = "Mieter";
    /**
     * Rent
     */
    String RENT_YEAR = "AbJahr";
    /**
     * Heater
     */
    String HEATER_COST = "Betrag";
    /**
     * Heater
     */
    String HEATER_TABLE = "Heizkosten";
    String PREPAYED_HEATER_TABLE = "VorkasseHeizkosten";
    /**
     * Heater
     */
    String HEATER_YEAR = "Abrechnungsjahr";
    String HEATER_TENANT = "Mieter";
    String PREPAYED_HEATER_TENANT = HEATER_TENANT, PREPAYED_HEATER_YEAR = HEATER_YEAR,
	    PREPAYED_HEATER_PAYED = HEATER_COST;

    /**
     * Base Tax
     */
    String BASE_TAX_TABLE = "Grundsteuer", BASE_TAX_HOUSE = "ID_Haus", BASE_TAX_YEAR = "Jahr" + "",
	    BASE_TAX_VALUE = "Betrag";

    String GARDEN_TABLE = "Gartenarbeit", GARDEN_DATE = "Datum", GARDEN_VALUE = "Betrag";

    /**
     * Payments
     */
    String PAYMENT_TABLE = "Mieterfinanzen", PAYMENT_TENANT = "Mieter", PAYMENT_DATE = "Datum",
	    PAYMENT_VALUE = "Einzahlung";

    /**
     * Garage
     */

    String GARAGE_TABLE = "Garagen", GARAGE_ID = "id_Garage", GARAGE_NAME = "Bezeichnung";

    String GARAGE_CONTRACT_TABLE = "GaragenMieter", GARAGE_CONTRACT_ID = "id_GaragenMiete",
	    GARAGE_CONTRACT_GARAGE = "id_Garage", GARAGE_CONTRACT_START = "Einzug", GARAGE_CONTRACT_RENT = "Miete",
	    GARAGE_CONTRACT_TENANT = "id_Mieter";

    String GARAGE_EXTERN_TABLE = "ExterneGaragenMieter", GARAGE_EXTERN_GARAGE = "id_Garage",
	    GARAGE_EXTERN_TENANT = "Mieter", GARAGE_EXTERN_RENT = "Miete", GARAGE_EXTERN_DATE = "Einzug";

    String GARAGE_EXTRA_TABLE = "GarageNebenkosten", GARAGE_EXTRA_ID = "ID", GARAGE_EXTRA_TYPE = "Bezeichnung",
	    GARAGE_EXTRA_VALUE = "Betrag", GARAGE_EXTRA_YEAR = "Jahr";

    String STELLP_TABLE = "Stellplätze", STELLP_ID = "ID_Stellplatz", STELLP_NAME = "Bezeichnung";

    String STELLP_CONTRACT_TABLE = "StellplatzVertrag", STELLP_CONTRACT_ID = "ID_Vertrag",
	    STELLP_CONTRACT_STELLPLATZ = "ID_Stellplatz", STELLP_CONTRACT_TENANT = "ID_Mieter",
	    STELLP_CONTRACT_START = "Start", STELLP_CONTRACT_RENT = "Miete";

    String STELLP_EXTERN_TABLE = "StellplatzExternerVertrag", STELLP_EXTERN_ID = "ID_ExternerVertrag",
	    STELLP_EXTERN_STELLPLATZ = "ID_Stellplatz", STELLP_EXTERN_TENANT = "Mieter", STELLP_EXTERN_START = "Start",
	    STELLP_EXTERN_RENT = "Miete";

    String SONSTIGE_TABLE = "Sonstige_Kosten", SONSTIGE_ID = "ID_sonstige", SONSTIGE_TENANT = "Mieter",
	    SONSTIGE_VALUE = "Betrag", SONSTIGE_YEAR = "Jahr", SONSTIGE_DESCRIPTION = "Bezeichnung";

    String MODERN_TABLE = "Modernisierung", MODERN_ID = "ID_modern", MODERN_TENANT = "Mieter", MODERN_VALUE = "Betrag",
	    MODERN_YEAR = "Jahr", MODERN_DESCRIPTION = "Bezeichnung";

}
