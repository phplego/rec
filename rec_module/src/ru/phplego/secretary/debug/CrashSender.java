package ru.phplego.secretary.debug;

import org.acra.CrashReportData;
import org.acra.sender.GoogleFormSender;
import org.acra.sender.ReportSenderException;

/**
* Created with IntelliJ IDEA.
* User: Oleg
* Date: 14.04.12
* Time: 18:33
* To change this template use File | Settings | File Templates.
*/

public class CrashSender extends GoogleFormSender {
	public CrashSender(String formKey) {
		super(formKey);
	}

	public void send(CrashReportData report) throws ReportSenderException {
		super.send(report);
	}

}
