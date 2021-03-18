package xyz.theprogramsrc.supermanager;

import xyz.theprogramsrc.supercoreapi.global.translations.Translation;
import xyz.theprogramsrc.supercoreapi.global.translations.TranslationManager;
import xyz.theprogramsrc.supercoreapi.global.translations.TranslationPack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum L implements TranslationPack {

    /* Texts */
    DOWNLOAD("Download"),
    DESCRIPTION("Description"),
    AUTHOR("Author"),
    SUPPORTED_VERSIONS("Supported Versions"),
    UNKNOWN_VERSION("Unknown version"),
    OPEN_IN_BROWSER("Open in Browser"),
    /* Messages */
    MODULE_DISABLED("&cThe module &9{ModuleName}&c is disabled! To use this feature you need to enable it"),
    NO_ACCESS_TO_MODULE("&cYou don't have access to the module &9{ModuleName}&c! You need the permission &b{ModulePermission} &cto use it."),
    NO_CONNECTION("&cThere is no internet connection. Please try later"),
    ERROR_WHILE_FETCHING_LATEST_VERSION("&cAn unknown error occurred while trying to fetch the latest version of &7{PluginName}&c. Please try again later."),
    PLUGIN_UP_TO_DATE("&aThe plugin &7{PluginName}&a is up to date!"),
    PLUGIN_NEEDS_UPDATE("&cThe plugin &7{PluginName}&c needs to be updated from the version &7{CurrentVersion}&c to the version &a{LatestVersion}"),
    DOWNLOADING_UPDATE("&aThe update download for &7{PluginName}&a has been started."),
    ERROR_ON_DOWNLOAD("&cError while downloading the update for &7{PluginName}&c. You'll find more information in the console."),
    SUCCESS_DOWNLOAD("&aThe download for the plugin &7{PluginName}&a has been successfully finished. Please restart the server to apply the changes."),
    TOKEN_WILL_NOT_BE_SHARED("&aDon't worry, your token will only be available in the config file and we won't share it to anyone."),
    TOKEN_SAVED("&aThe token was saved in the config."),
    MODULE_STATUS_UPDATED("&aThe new status of the module &7{ModuleName} &ais: &7{ModuleStatus}"),
    ERROR_ON_PRODUCT_DOWNLOAD("&cError while downloading the product &7{ProductName}&c (&7{ProductVersion}&c). You'll find more information in the console."),
    DOWNLOADING_PRODUCT("&aThe product download &7{ProductName}&a has been started."),
    SUCCESS_PRODUCT_DOWNLOAD("&aThe download for the product &7{ProductName}&a has been successfully finished."),
    PRODUCT_VERSION_URL("&aClick the following link to see the product version in your browser: &7{ProductURL}"),
    PRODUCT_DOWNLOAD_IS_URL("&cThe product has an url as download and we cannot verify the source. For your security we will not download this file. You will need to download it manually."),

    /* Dialogs */

    DIALOG_TOKEN_INPUT_TITLE("&9Token"),
    DIALOG_TOKEN_INPUT_SUBTITLE("&7Type in your songoda token"),
    DIALOG_TOKEN_INPUT_ACTIONBAR("&aThis will let the plugin download premium products."),

    DIALOG_SONGODA_MARKETPLACE_SEARCH_TITLE("&9Search"),
    DIALOG_SONGODA_MARKETPLACE_SEARCH_SUBTITLE("&7Type in a search term"),
    DIALOG_SONGODA_MARKETPLACE_SEARCH_ACTIONBAR("&aSearch through the songoda marketplace!"),
    PLUGIN_MANAGER_NOT_RECOMMENDED_MAY_CAUSE_MALFUNCTION("&cNOT RECOMMENDED, MAY CRASH OTHER PLUGINS."),
    PLUGIN_MANAGER_PLUGIN_ALREADY_ENABLED("&cThe plugin &7{PluginName} &cis already enabled."),
    PLUGIN_MANAGER_PLUGIN_ALREADY_DISABLED("&cThe plugin &7{PluginName} &cis already disabled."),
    PLUGIN_MANAGER_PLUGIN_FAILED_TO_ENABLE("&cFailed to enable &7{PluginName}&c."),
    PLUGIN_MANAGER_PLUGIN_FAILED_TO_DISABLE("&cFailed to disable &7{PluginName}&c."),
    PLUGIN_MANAGER_PLUGIN_ENABLED("&aSuccessfully enabled &7{PluginName}&a."),
    PLUGIN_MANAGER_PLUGIN_DISABLED("&aSuccessfully disabled &7{PluginName}&a."),
    PLUGIN_MANAGER_CHECKING_FOR_UPDATES("&aChecking for &7{PluginName}&a updates..."),
    PLUGIN_MANAGER_FAILED_TO_CHECK_FOR_UPDATES("&cFailed to check for &7{PluginName}&c updates."),
    PLUGIN_MANAGER_NEW_UPDATE_AVAILABLE("&aA new update (&7{NewVersion}&a)is available for &7{PluginName}&a!"),
    PLUGIN_MANAGER_ALREADY_UP_TO_DATE("&aPlugin already up to date!"),
    PLUGIN_MARKETPLACE_STILL_LOADING("&cThe Plugin marketplace module is still loading products, you might not find every product that is currently available in the marketplace."),
    PLUGIN_MARKETPLACE_CANNOT_DOWNLOAD_PAID_PLUGIN("&cYou cannot download paid plugins."),
    PLUGIN_MARKETPLACE_MESSAGE_RESPONSE("&cReceived the following response from the website: &7{Message}"),
    PLUGIN_MARKETPLACE_DOWNLOADING_PLUGIN("&aDownloading plugin..."),
    PLUGIN_MARKETPLACE_INSTALLING_PLUGIN("&aInstalling plugin..."),
    PLUGIN_MARKETPLACE_PLUGIN_ENABLED("&aThe plugin was successfully enabled!"),
    PLUGIN_MARKETPLACE_PLUGIN_ENABLE_WARNING("&6WARNING: IS RECOMMENDED TO RESTART THE SERVER TO AVOID BUGS BECAUSE THE PLUGIN ENABLING BY &9SuperManager &6IS NOT AN OFFICIAL WAY TO ENABLE A PLUGIN"),
    PLUGIN_MARKETPLACE_FAILED_TO_ENABLE_PLUGIN("&cError while enabling the plugin &7{PluginName}&c. You will find more info in the console."),
    PLUGIN_MARKETPLACE_FAILED_PLUGIN_DOWNLOAD("&cYou cannot download paid plugins."),
    PLUGIN_MARKETPLACE_PLUGIN_ALREADY_INSTALLED("&cThe plugin &7{PluginName}&c is already installed!"),
    PLUGIN_MARKETPLACE_INVALID_FILE("&cThe download file is not a jar file, you need to install it manually. You will find the download in the path &7{Path}"),

    /* Modules */
    PLUGIN_MANAGER_DISPLAY("Plugin Manager"),
    PLUGIN_MANAGER_NAME("&aPlugin Manager"),
    PLUGIN_MANAGER_LORE("&7Manage your Plugins in-game"),

    SONGODA_MARKETPLACE_DISPLAY("Songoda Marketplace"),
    SONGODA_MARKETPLACE_NAME("&cSongoda Marketplace"),
    SONGODA_MARKETPLACE_LORE("&7The &cSongoda Marketplace&7 but in-game!"),


    /* GUIs */
    MODULE_MANAGER_DISABLE_ACTION("Disable Module"),
    MODULE_MANAGER_ENABLE_ACTION("Enable Module"),

    /* Plugin Manager */
    PLUGIN_MANAGER_BROWSER_TITLE("&aPlugins"),
    PLUGIN_MANAGER_BROWSER_ITEM_NAME("&a{PluginName}"),
    PLUGIN_MANAGER_BROWSER_ITEM_LORE("&7Manage plugin &a{PluginName} &7(&c{PluginVersion}&7)"),

    PLUGIN_VIEW_TITLE("&aPlugins&7 > &c{PluginName}"),
    PLUGIN_VIEW_CHECK_UPDATE_ITEM_NAME("&aCheck for Updates"),
    PLUGIN_VIEW_CHECK_UPDATE_ITEM_LORE("&7Click to check for updates."),

    PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_NAME("&aDownload Update"),
    PLUGIN_VIEW_DOWNLOAD_UPDATE_ITEM_LORE("&7Click to download the latest update for &a{PluginName}"),

    PRODUCT_VIEW_TITLE("&aProducts&7 > &c{ProductName}"),
    PRODUCT_VIEW_CARD_NAME("&a{ProductName}"),
    PRODUCT_VIEW_CARD_AUTHOR("&a{ProductAuthor}"),
    PRODUCT_VIEW_CARD_TAGLINE("&7{ProductTagline}"),
    PRODUCT_VIEW_CARD_DESCRIPTION("&e{ProductDescription}"),
    PRODUCT_VIEW_CARD_SUPPORTED_VERSIONS("&e{SupportedVersions}"),

    PRODUCT_VERSION_BROWSER_TITLE("&a{ProductName} &7> &cVersions"),
    PRODUCT_VERSION_BROWSER_VERSION_NAME("&a{VersionName}"),


    PLUGIN_MANAGER_BROWSER_REFRESH_NAME("&aRefresh Cache"),
    PLUGIN_MANAGER_BROWSER_REFRESH_LORE("&7Click to refresh the cache"),
    PLUGIN_MANAGER_BROWSER_CACHE_RELOADED("&aCache reloaded."),

    PLUGIN_MANAGER_VIEW_TITLE("&aPlugins &8> &c{PluginName}"),
    PLUGIN_MANAGER_VIEW_ENABlE_NAME("&aEnable Plugin"),
    PLUGIN_MANAGER_VIEW_ENABlE_LORE("&7Click to &7enable&a the plugin"),
    PLUGIN_MANAGER_VIEW_DISABLE_NAME("&cDisable Plugin"),
    PLUGIN_MANAGER_VIEW_DISABLE_LORE("&7Click to&c disable &7the plugin"),
    PLUGIN_MANAGER_CHECK_UPDATES_NAME("&aCheck for updates"),
    PLUGIN_MANAGER_CHECK_UPDATES_LORE("&7Click to check for updates"),
    ;

    private TranslationManager translationManager;
    private final String value;

    L(String value){
        this.value = value;
    }

    @Override
    public Locale getLanguage() {
        return new Locale("en","US");
    }

    @Override
    public Translation get() {
        return new Translation(this, name(), value);
    }

    @Override
    public List<Translation> translations() {
        return Arrays.stream(values()).map(L::get).collect(Collectors.toList());
    }

    @Override
    public void setManager(TranslationManager manager) {
        this.translationManager = manager;
    }

    @Override
    public TranslationManager getManager() {
        return this.translationManager;
    }

    @Override
    public String toString() {
        return this.get().translate();
    }
}
