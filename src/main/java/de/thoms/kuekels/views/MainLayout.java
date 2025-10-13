package de.thoms.kuekels.views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Whitespace;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

import de.thoms.ThomsDialog01;
import de.thoms.kuekels.GlobaleWerte;
import de.thoms.kuekels.data.User;
import de.thoms.kuekels.data.UserRepository;
import de.thoms.kuekels.security.AuthenticatedUser;
import de.thoms.kuekels.views.alleFaenge.AlleFaengeView;
import de.thoms.kuekels.views.benutzerprofil.BenutzerprofilView;
import de.thoms.kuekels.views.fangbuch.FangbuchView;
import de.thoms.kuekels.views.fangeingabe.FangEingabeView;
import de.thoms.kuekels.views.letzteFaenge.LetzteFaenge;
import de.thoms.kuekels.views.meldungen.Meldungen;
import de.thoms.kuekels.security.Benutzerverwaltung;
import de.thoms.kuekels.views.statistik.StatisikView;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@Route("main")
@AnonymousAllowed
public class MainLayout extends AppLayout {

	/**
	 * A simple navigation item component, based on ListItem element.
	 */
	public static class MenuItemInfo extends ListItem {

		private final Class<? extends Component> view;

		public MenuItemInfo(String menuTitle, Component icon, Class<? extends Component> view) {
			this.view = view;
			RouterLink link = new RouterLink();
			// Use Lumo classnames for various styling
			link.addClassNames(Display.FLEX, Gap.XSMALL, Height.MEDIUM, AlignItems.CENTER, Padding.Horizontal.SMALL,
					TextColor.BODY);
			link.setRoute(view);

			Span text = new Span(menuTitle);
			// Use Lumo classnames for various styling
			text.addClassNames(FontWeight.MEDIUM, FontSize.MEDIUM, Whitespace.NOWRAP);

			if (icon != null) {
				link.add(icon);
			}
			link.add(text);
			add(link);
		}

		public Class<?> getView() {
			return view;
		}

	}

	private AuthenticatedUser authenticatedUser;
	private AccessAnnotationChecker accessChecker;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;

	public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker,
			UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.authenticatedUser = authenticatedUser;
		this.accessChecker = accessChecker;
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;

		DrawerToggle toggle = new DrawerToggle();
		addToNavbar(toggle); // ← Burger-Button

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean istAngemeldet = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());

		VerticalLayout menu1 = new VerticalLayout();

		if (istAngemeldet) {
			menu1.add(new RouterLink("Fangeingabe", FangEingabeView.class));
			menu1.add(new RouterLink("Mein Fangbuch", FangbuchView.class));
			menu1.add(new RouterLink("Benutzerprofil", BenutzerprofilView.class));
		}
		menu1.add(new RouterLink("Alle Fänge", AlleFaengeView.class));
		menu1.add(new RouterLink("Letzte Fänge", LetzteFaenge.class));
		menu1.add(new RouterLink("News", Meldungen.class));

		if (hasRole("ADMIN")) {
			menu1.add(new H3("Admin"));
			menu1.add(new RouterLink("Benutzerverwaltung", Benutzerverwaltung.class));
			// menu1.add(new RouterLink("Neuer Benutzer", e -> openUserDialog()));
			Button neuerBenutzer = new Button("Neuer Benutzer", e -> openUserDialog());
			neuerBenutzer.getStyle().set("background", "none").set("border", "none")
					.set("color", "var(--lumo-primary-text-color)").set("cursor", "pointer").set("padding", "0")
					.set("text-align", "left");
			neuerBenutzer.addClassName("link-button");

			Button showDialogVersion = new Button("Versionshinweis", e -> openUserVersionDialog());
			showDialogVersion.getStyle().set("background", "none").set("border", "none")
					.set("color", "var(--lumo-primary-text-color)").set("cursor", "pointer").set("padding", "0")
					.set("text-align", "left");

			showDialogVersion.addClassName("link-button");

			menu1.add(neuerBenutzer, showDialogVersion);
		}

		if (hasRole("ADMIN") | hasRole("STATISTIK")) {
			menu1.add(new H3("Statistik"));
			menu1.add(new RouterLink("Statistik", StatisikView.class));

		}

		addToDrawer(menu1);
		MenuBar menu = new MenuBar();
		menu.setWidth("min-content");
		// setMenuData(menu);
		addToNavbar(createHeaderContent(menu));
	}

	private void openUserVersionDialog() {

		ThomsDialog01 meinDialog = new ThomsDialog01();
		meinDialog.setContext(GlobaleWerte.Aenderungen);
		meinDialog.setTop("Aktuelle Version <h2>" + GlobaleWerte.Version + "</h2>");
		
		Dialog dialog = meinDialog.erzeugeDialog();
		//dialog.setHeight("200px");
		
		dialog.open();
	}

	private void openUserDialog() {
		Dialog dialog = new Dialog();
		dialog.setHeaderTitle("Neuen Benutzer erstellen");
		TextField usernameField = new TextField("Benutzername");
		TextField emailField = new TextField("E-Mail");
		TextField telefonField = new TextField("Telefon");

		Button saveButton = new Button("Erstellen", event -> {
			String username = usernameField.getValue();
			String password = "angeln";
			String email = emailField.getValue();
			String telefon = telefonField.getValue();

			if (username.isEmpty() || password.isEmpty()) {
				Notification.show("Benutzername und Passwort sind erforderlich.", 3000, Notification.Position.MIDDLE);
				return;
			}

			if (userRepository.findByUsername(username).isPresent()) {
				Notification.show("Benutzername existiert bereits.", 3000, Notification.Position.MIDDLE);
				return;
			}

			User user = new User();
			user.setUsername(username);
			user.setHashedPassword(passwordEncoder.encode(password));
			user.setEmail(email);
			user.setTelefon(telefon);

			userRepository.save(user);
			Notification.show("Benutzer erfolgreich erstellt!", 3000, Notification.Position.TOP_CENTER);
			dialog.close();
		});

		saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		Button cancelButton = new Button("Abbrechen", e -> dialog.close());
		cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

		HorizontalLayout buttons = new HorizontalLayout(cancelButton, saveButton);
		VerticalLayout formLayout = new VerticalLayout(usernameField, emailField, telefonField);

		dialog.add(formLayout, buttons);
		dialog.open();
	}

	public boolean hasRole(String role) {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).anyMatch(r -> r.equals("ROLE_" + role));
	}

	private Component createHeaderContent(MenuBar menu) {
		Header header = new Header();
		header.addClassNames(BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN, Width.FULL);

		Div layout = new Div();
		layout.addClassNames(Display.FLEX, AlignItems.CENTER, Padding.Horizontal.LARGE);

		H1 appName = new H1(GlobaleWerte.Version);

		appName.addClassNames(Margin.Vertical.MEDIUM, Margin.End.AUTO, FontSize.LARGE, Margin.Horizontal.AUTO);
		layout.add(menu);
		layout.add(appName);

		Optional<User> maybeUser = authenticatedUser.get();
		if (maybeUser.isPresent()) {
			User user = maybeUser.get();
			Avatar avatar = new Avatar(user.getName());

			MenuBar userMenu = new MenuBar();
			userMenu.setThemeName("tertiary-inline contrast");

			MenuItem userName = userMenu.addItem("");
			Div div = new Div();

			try {

				StreamResource resource = new StreamResource("profile-pic",
						() -> new ByteArrayInputStream(user.getProfilePicture()));
				avatar.setImageResource(resource);
				avatar.setThemeName("xsmall");
				avatar.getElement().setAttribute("tabindex", "-1");
				div.add(avatar);
			} catch (Exception e) {
				System.out.println();
			}

			div.add(user.getName());
			div.add(new Icon("lumo", "dropdown"));
			div.addClassNames(LumoUtility.Display.FLEX, LumoUtility.AlignItems.CENTER, LumoUtility.Gap.SMALL);
			userName.add(div);
			userName.getSubMenu().addItem("Abmelden", e -> {
				authenticatedUser.logout();
			});
			userName.getSubMenu().addItem("Passwort ändern", e1 -> {
				Dialog dialog = new Dialog();

				PasswordField currentPassword = new PasswordField("Aktuelles Passwort");
				PasswordField newPassword = new PasswordField("Neues Passwort");
				PasswordField confirmPassword = new PasswordField("Neues Passwort bestätigen");
				Span feedback = new Span();
				feedback.getStyle().set("color", "red");

				Button pswAendern = new Button("Passwort ändern", event -> {
					if (maybeUser.isPresent()) {

						if (!passwordEncoder.matches(currentPassword.getValue(), user.getHashedPassword())) {
							feedback.setText("Aktuelles Passwort ist falsch.");
						} else if (!newPassword.getValue().equals(confirmPassword.getValue())) {
							feedback.setText("Neue Passwörter stimmen nicht überein.");
						} else {
							user.setHashedPassword(passwordEncoder.encode(newPassword.getValue()));
							userRepository.save(user);
							feedback.setText("Passwort wurde erfolgreich geändert.");
							feedback.getStyle().set("color", "green");
						}
					}
					dialog.close();

				});

				VerticalLayout dialogLayout = new VerticalLayout(currentPassword, newPassword, confirmPassword,
						pswAendern);

				dialog.add(dialogLayout);
				dialog.open();
			});

			layout.add(userMenu);
		} else {
			Anchor loginLink = new Anchor("login", "Anmelden");
			layout.add(loginLink);
		}

		header.add(layout);
		return header;
	}

}
