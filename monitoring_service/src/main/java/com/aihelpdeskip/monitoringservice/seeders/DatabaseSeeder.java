package com.aihelpdeskip.monitoringservice.seeders;

import java.util.Optional;

import com.aihelpdeskip.monitoringservice.models.Role;
import com.aihelpdeskip.monitoringservice.models.User;
import com.aihelpdeskip.monitoringservice.models.VRTTemplate;
import com.aihelpdeskip.monitoringservice.repository.UserRepository;
import com.aihelpdeskip.monitoringservice.repository.VRTTemplateRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder {
    private VRTTemplateRepository vrtTemplateRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DatabaseSeeder(
            VRTTemplateRepository vrtTemplateRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
        ) {
        this.vrtTemplateRepository = vrtTemplateRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener
    public void seed(ContextRefreshedEvent event) {

        if( vrtTemplateRepository.findAll().size() == 0 ) {
                String language = "eng";

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "A",
                                "The status of the invoice(s) you are inquiring about can be displayed on our IPAY portal at <a href=\"https://launchpad.ipaper.com\">https://launchpad.ipaper.com</a>. IPAY is the new, improved and very user friendly website designed for International Paper’s suppliers to check both pending and past due payments.<br>\n" +
                                        "Please utilize this tool as your primary source of information. Checking for payment status online is not only the best practice for most of International Paper suppliers, but also our management policy &ndash; IPAY should always be your first point of reference. \n" +
                                        "<br><br>Useful hints:<br><br>\n" +
                                        "1. The website has real time information, which allows you to stay current with the status of your invoices.<br>\n" +
                                        "2. It provides the option to generate missing payment details.<br>\n" +
                                        "3. When searching for an invoice by its reference number, you need to omit special characters and replace letters \"O\" with number \"0\".<br>\n" +
                                        "4. Invoice date field is required to search for an invoice or payment, date range must be input in six month increments.<br>\n" +
                                        "5. It is recommended to use Google Chrome to access the website.\n" +
                                        "<br><br>\n" +
                                        "<strong>If you do not have access IPAY yet, please contact IPAY Administrators Team at <a href=\"mailto:IPAYAccess@ipaper.com\">IPAYAccess@ipaper.com</a> to find out what is needed to obtain login information.</strong>",
                                "Invoice Blocked",
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "B",
                                "Your invoice has been addressed with the facility. If you do not receive payment within 15 business days and invoice is already past due please"
                                + " follow up with us at accounts.payable@ipaper.com.", 
                                "Invoice Awaiting Processing",
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "C",
                                "Invoice is in processing queue and will be posted shortly. If you do not receive payment within next 15 business days and invoice is already"
                                + " past due please follow up with us.", 
                                "Invoice awaiting processing",
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "D",
                                "Please be informed that the invoice(s) in question could not be found in our database. Please email their copies to <a href=\"mailto:IPaccounts.payable@ipaper.com\">IPaccounts.payable@ipaper.com</a> which is correct email address for submitting invoices for processing (Exception: Invoices billed to <strong>Ariba Purchase Orders*</strong>). Please allow 7 business days for processing of your invoices from the date they were submitted.\n" +
                                        "<br><br>\n" +
                                        "Be sure to observe the following conditions when submitting invoices to the above address:\n" +
                                        "<br><br>\n" +
                                        "1. Invoices must be submitted as an attachment.<br>\n" +
                                        "2. One invoice per attachment. Multiple attachments per email are acceptable.<br>\n" +
                                        "3. Invoices must be in either .PDF or .TIF format. No other formats are acceptable at this time. Any invoices received using an invalid format will be discarded.<br>\n" +
                                        "4. Do not mail a duplicate copy of the invoice if using email.<br>\n" +
                                        "5. Do not include electronic signatures in the body of the email. This will cause an email failure.\n" +
                                        "<br><br>\n" +
                                        "If email submission is not possible, invoices should be mailed to:<br>\n" +
                                        "International Paper Shared Services<br>\n" +
                                        "PO Box 5383<br>\n" +
                                        "Portland, OR 97228-5383\n" +
                                        "<br><br>\n" +
                                        "Status of invoices can be displayed on our IPAY supplier portal at <a href=\"https://launchpad.ipaper.com\">https://launchpad.ipaper.com</a>.<br>\n" +
                                        "If you do not have access to IPAY yet, please contact IPAY Administrators Team at <a href=\"mailto:IPAYAccess@ipaper.com\">IPAYAccess@ipaper.com</a> to find out what is needed to obtain login information.\n" +
                                        "<br><br>\n" +
                                        "Do not send statements or other correspondence to the PO box or email address stated above. Vendor Statements should be emailed to <a href=\"mailto:IPvendor.statements@ipaper.com\">IPvendor.statements@ipaper.com</a>.\n" +
                                        "<br><br>\n" +
                                        "Vendor Inquiries should be sent to <a href=\"mailto:accounts.payable@ipaper.com\">accounts.payable@ipaper.com</a>\n" +
                                        "<br><br>\n" +
                                        "<strong>*Ariba Purchase Orders:</strong>\n" +
                                        "<br><br>\n" +
                                        "If your company joined the Ariba Network with International Paper, invoice copies for Purchase Orders sent through Ariba should not have been submitted via e-mail. They need to be created directly via the Ariba Supplier Portal which gives your company all the tools needed to confirm our orders, create invoices, cancel invoices, submit credit memos and send notices of shipment.\n" +
                                        "<br><br>\n" +
                                        "Please access your ARIBA profile to display the order, create an invoice and submit it to International Paper at <a href=\"https://supplier.ariba.com\">https://supplier.ariba.com</a>.",
                                "Invoice Missing",
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "E",
                                "VRT skipped - missing information", 
                                null,
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "F",
                                "Manual", 
                                null,
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "G",
                                "Dear Valued Supplier,<br><br>Your invoice is currently blocked for payment while being reviewed by our Contracted Services Department. It is"
                                + " standard procedure for contracted services invoices to be verified with regards to rates and contract terms.<br><br>Hello CS"
                                + " Team,<br><br>Invoice mentioned in e-mail below is currently on your audit hold. Please communicate with the vendor in case you need any"
                                + " additional backup in order to release the invoice for payment.", 
                                null,
                                language
                        )
                );
                
                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "A2",
                                "The status of the invoice(s) you are inquiring about can be displayed on our IPAY portal at <a href=\"https://launchpad.ipaper.com\">https://launchpad.ipaper.com</a>. IPAY is the new, improved and very user friendly website designed for International Paper’s suppliers to check both pending and past due payments.<br>\n" +
                                        "Please utilize this tool as your primary source of information. Checking for payment status online is not only the best practice for most of International Paper suppliers, but also our management policy &ndash; IPAY should always be your first point of reference. \n" +
                                        "<br><br>Useful hints:<br><br>\n" +
                                        "1. The website has real time information, which allows you to stay current with the status of your invoices.<br>\n" +
                                        "2. It provides the option to generate missing payment details.<br>\n" +
                                        "3. When searching for an invoice by its reference number, you need to omit special characters and replace letters \"O\" with number \"0\".<br>\n" +
                                        "4. Invoice date field is required to search for an invoice or payment, date range must be input in six month increments.<br>\n" +
                                        "5. It is recommended to use Google Chrome to access the website.\n" +
                                        "<br><br>\n" +
                                        "<strong>If you do not have access IPAY yet, please contact IPAY Administrators Team at <a href=\"mailto:IPAYAccess@ipaper.com\">IPAYAccess@ipaper.com</a> to find out what is needed to obtain login information.</strong>",
                                "Invoice Status Provided",
                                language
                        )
                );

                language = "spa";

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "A1",
                                "Buenos días,<br/>Les informamos que la factura fue pagada el día {}. Por favor, tengan en cuenta que la "
                                +"transferencia tarda dos días hábiles en llegar.<br/><br/>Un cordial saludo,<br/>Cuentas a pagar",
                                "Invoice Status Provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "A2",
                                "Buenos días,<br/>Les informamos que las facturas fueron pagadas el día {}. Por favor, tengan en cuenta "
                                +"que la transferencia tarda dos días hábiles en llegar.<br/><br/>Un cordial saludo,<br/>Cuentas a pagar",
                                "Invoice Status Provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "B",
                                "Buenos dias,<br/>Dicha factura no está registrada en nuestro sistema<br/>Por favor, envíen una copia en formato"
                                +" PDF a scanning.spain@ipaper.com.<br/><br/>Gracias.<br/>Un cordial saludo,",
                                "Invoice Missing",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "C1",
                                "Buenos días,<br/>Su factura {} se encuentra pendiente de verificación por parte de la planta. Por favor si esta "
                                +"factura se encuentra vencida, pónganse en contacto con nosotros en cuentas.apagar@ipaper.com.<br/><br/>Un cordial saludo,"
                                +"<br/>Cuentas a pagar",
                                "Invoice blocked",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "C2",
                                "Buenos días,<br/>Sus facturas se encuentran pendientes de verificación por parte de la planta. Por favor si estas facturas "
                                +"se encuentran vencida, pónganse en contacto con nosotros.<br/><br/>Un cordial saludo,<br/>Cuentas a pagar",
                                "Invoice blocked",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "D1",
                                "Buenos dias,<br/>Su factura se encuentra procesada y será pagada, según las condiciones de pago acordadas con la planta, el "
                                +"día {}.<br/><br/>Un cordial saludo,<br/>Cuentas a pagar",
                                "Invoice status provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "D2",
                                "Buenos dias,<br/>Sus facturas se encuentran procesadas y serán pagadas, según las condiciones de pago acordadas con la planta, "
                                +"el día {}.<br/><br/>Un cordial saludo,<br/>Cuentas a pagar",
                                "Invoice status provided",
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "F",
                                "Manual", 
                                null,
                                language
                        )
                );

                language = "fra";

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "A1",
                                "Bonjour,<br/>Votre facture a ete reglee par virement le {}<br/><br/>Cordialement,<br/>Comptabilite Fournisseurs",
                                "Invoice Status Provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "A2",
                                "Bonjour,<br/>Vos factures ont ete reglees par virement le {}<br/><br/>Cordialement,<br/>Comptabilite Fournisseurs",
                                "Invoice Status Provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "B",
                                "Bonjour,<br/>nous n'avons pas recu la facture {} relancee.<br/>Veuillez nous transmettre une copie certifiee conforme"
                                +", en version papier, par la post a l'adresse de PO Box en Pologne.<br/>Nous ne pouvons pas comptabiliser  les factures"
                                +" recues par e-mail.<br/><br/>Cordialement,<br/>Comptabilite Fournisseurs",
                                "Invoice Missing",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "C1",
                                "Bonjour,<br/>Votre facture {} est en cours de validation.<br/><br/>Cordialement,<br/>Comptabilite Fournisseurs",
                                "Invoice blocked",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "C2",
                                "Bonjour,<br/>Vos factures sont en cours de validation.<br/><br/>Cordialement,<br/>Comptabilite Fournisseurs",
                                "Invoice blocked",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "D1",
                                "Bonjour,<br/>Votre facture  est  approuvee et devrait etre payee le {}<br/><br/>Cordialement,<br/>Comptabilite Fournisseurs",
                                "Invoice status provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "D2",
                                "Bonjour,<br/>Vos factures sont approuvees et devraient etre  payees le {}<br/><br/>Cordialement<br/>Comptabilite Fournisseurs",
                                "Invoice status provided",
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "F",
                                "Manual", 
                                null,
                                language
                        )
                );

                language = "ita";

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "A1",
                                "Buongiorno,<br/>La Sua fattura è stata pagata tramite bonifico bancario sul conto n. {}<br/><br/>Cordiali saluti,<br/>Contabilità Fornitori",
                                "Invoice Status Provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "A2",
                                "Buongiorno,<br/>Le Sue fatture sono state pagate tramite bonifico bancario sul conto n. {}<br/><br/>Cordiali saluti,<br/>Contabilità Fornitori",
                                "Invoice Status Provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "B1",
                                "Buongiorno,<br/>Non abbiamo ricevuto la Vostra fattura n{}<br/>Vi preghiamo cortesemente di verificare la ricevuta di "
                                +"consegna e di inviarcela all'indirizzo di posta contabilita.fornitori@ipaper.com mentre nel caso in cui non abbiate l’obbiligo "
                                +"della fatturazione elettronica Vi chiediamo gentilmente di inviarci la copia presso la nostra casella di posta precedentemente "
                                +"indicata.<br/><br/>Grazie per la collaborazione.<br/>Cordiali saluti.",
                                "Invoice Missing",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "B2",
                                "Buongiorno,<br/>Non abbiamo ricevuto le Vostre fatture n{}<br/>Vi preghiamo cortesemente di verificare le ricevute di consegna "
                                +"e di inviarcele all'indirizzo di posta contabilita.fornitori@ipaper.com mentre nel caso in cui non abbiate l’obbiligo della "
                                +"fatturazione elettronica Vi chiediamo gentilmente di inviarci le copie presso la nostra casella di posta precedentemente indicata."
                                +"<br/><br/>Grazie per la collaborazione.<br/>Cordiali saluti.",
                                "Invoice Missing",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "C1",
                                "Buongiorno,<br/>La Vostra fattura {} è in fase di verifica.<br/><br/>Cordiali saluti,<br/>Contabilità Fornitori",
                                "Invoice blocked",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "C2",
                                "Buongiorno,<br/>Le  Vostre fatture sono in fase di verifica.<br/><br/>Cordiali saluti,<br/>Contabilità Fornitori",
                                "Invoice blocked",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "D1",
                                "Buongiorno,<br/>La Sua fattura é stata registrata e verrà  saldata con il pagamento automatico del {}<br/><br/>"
                                +"Cordiali saluti<br/>Contabilità Fornitori",
                                "Invoice status provided",
                                language
                        )
                );

                vrtTemplateRepository.save( 
                        new VRTTemplate(
                                "D2",
                                "Buongiorno,<br/>Le Sue fatture sono state registrate e verranno saldate con il pagamento automatico del {}<br/><br/>"
                                +"Cordiali saluti<br/>Contabilità Fornitori",
                                "Invoice status provided",
                                language
                        )
                );

                vrtTemplateRepository.save(
                        new VRTTemplate(
                                "F",
                                "Manual", 
                                null,
                                language
                        )
                );
        }

        Optional<User> user = userRepository.findByUsername("admin");

        if (!user.isPresent()){
                User new_user = new User();
                new_user.setUsername("admin");
                new_user.setPassword(passwordEncoder.encode("@dm1n"));
                new_user.grantAuthority(Role.ROLE_ADMIN);
                new_user.setName("Administrator");
                new_user.setEmail("admin@capgemini.com");
                userRepository.save(new_user);
        }

        user = userRepository.findByUsername("osachs");
        if (!user.isPresent()){
                User new_user = new User();
                new_user.setUsername("osachs");
                new_user.setPassword(passwordEncoder.encode("Lato2019"));
                new_user.grantAuthority(Role.ROLE_ADMIN);
                new_user.setName("Olga Sachs");
                new_user.setEmail("agent@capgemini.com");
                userRepository.save(new_user);
        }

        user = userRepository.findByUsername("pmisiak");
        if (!user.isPresent()){
                User new_user = new User();
                new_user.setUsername("pmisiak");
                new_user.setPassword(passwordEncoder.encode("pmisiak"));
                new_user.grantAuthority(Role.ROLE_ADMIN);
                new_user.setName("Piotr Misiak");
                new_user.setEmail("agent@capgemini.com");
                userRepository.save(new_user);
        }

        user = userRepository.findByUsername("kturek");
        if (!user.isPresent()){
                User new_user = new User();
                new_user.setUsername("kturek");
                new_user.setPassword(passwordEncoder.encode("kturek"));
                new_user.grantAuthority(Role.ROLE_ADMIN);
                new_user.setName("Kacper Turek");
                new_user.setEmail("agent@capgemini.com");
                userRepository.save(new_user);
        }

        String[] newUsers = {"pzalewsk","JGIERA","MAGONTAR","DGORYCKI","PJUSKIEW","MARADAMC","AOCZKOS","DPAULUK","PWIDAJ","KAWOJCIK","ALIWROBE","JZYWCZAK"};
        for (String u : newUsers) {
        user = userRepository.findByUsername(u.toLowerCase());
        if (!user.isPresent()){
                User new_user = new User();
                new_user.setUsername(u.toLowerCase());
                new_user.setPassword(passwordEncoder.encode("SummeR@2020"));
                new_user.grantAuthority(Role.ROLE_ADMIN);
                new_user.setName(u.toLowerCase());
                new_user.setEmail(u.toLowerCase()+"@capgemini.com");
                userRepository.save(new_user);
        }
        }
    }
}
