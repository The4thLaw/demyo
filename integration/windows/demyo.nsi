!define MUI_CUSTOMFUNCTION_GUIINIT onPostInit

!include "MUI2.nsh"

# TODO: One fine day, add an uninstall option to remove user settings as well

# Variables and defines
!addplugindir plugins
!define DEMYO_REG_ROOT "HKCU"
!define DEMYO_REG_KEY "Software\Demyo"
!define DEMYO_REG_VALUE_VERSION "Version"
Var STARTMENU_FOLDER
Var SETUP_MODE # install / update

Name Demyo

# Branding
!define MUI_ICON Demyo.ico
!define MUI_HEADERIMAGE
	!define MUI_HEADERIMAGE_BITMAP nsis-header.bmp
	!define MUI_HEADERIMAGE_UNBITMAP nsis-header.bmp
!define MUI_WELCOMEFINISHPAGE_BITMAP nsis-welcome.bmp
!define MUI_UNWELCOMEFINISHPAGE_BITMAP nsis-welcome.bmp

XPStyle on # Safely ignored on Win2k
InstallDir "$PROGRAMFILES\Demyo"
InstallDirRegKey ${DEMYO_REG_ROOT} ${DEMYO_REG_KEY} ""
OutFile "Setup.exe"
SetCompressor /FINAL /SOLID lzma

!include x64.nsh
!include "unix2dos.nsh"

# Warn before exiting
!define MUI_ABORTWARNING

# Save some space at the start of the exe to load faster
!insertmacro MUI_RESERVEFILE_LANGDLL

# Save language for uninstallation
!define MUI_LANGDLL_REGISTRY_ROOT ${DEMYO_REG_ROOT}
!define MUI_LANGDLL_REGISTRY_KEY ${DEMYO_REG_KEY}
!define MUI_LANGDLL_REGISTRY_VALUENAME "Installation language"




# Pages for installation
!insertmacro MUI_PAGE_WELCOME
!insertmacro MUI_PAGE_LICENSE "..\..\LICENSE"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!define MUI_STARTMENUPAGE_REGISTRY_ROOT ${DEMYO_REG_ROOT}
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${DEMYO_REG_KEY}
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu folder"
!insertmacro MUI_PAGE_STARTMENU CreateStartMenuShortcuts $STARTMENU_FOLDER
!insertmacro MUI_PAGE_INSTFILES
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_FINISHPAGE_RUN # Must be defined for the option to show
!define MUI_FINISHPAGE_RUN_FUNCTION FinishLaunchDemyo
!define MUI_FINISHPAGE_RUN_TEXT $(SHELL_Launch_Demyo)
!insertmacro MUI_PAGE_FINISH


# Pages for uninstallation
!insertmacro MUI_UNPAGE_WELCOME
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES
!define MUI_FINISHPAGE_REBOOTLATER_DEFAULT
!insertmacro MUI_UNPAGE_FINISH

# Install demyo
Section "Demyo" COMP_Demyo
	# Pre-install/update functions
	StrCmp $SETUP_MODE "update" modePreUpdate modePreDone
	modePreUpdate:
		Call DemyoUpdatePre
	modePreDone:
	
	# Common instructions
	SetOutPath "$INSTDIR"

	File Demyo.bat
	File "..\..\source\demyo-app\target\Demyo.exe"
	File /oname=Readme.txt ..\..\README.md
	File /oname=License.txt ..\..\LICENSE
	DetailPrint "Changing newline characters"
	Push "$INSTDIR\Readme.txt"
	Call ConvertUnixNewLines
	Push "$INSTDIR\License.txt"
	Call ConvertUnixNewLines

	SetOutPath "$INSTDIR\war"
	File "..\..\source\demyo-web\target\*.war"
	
	SetOutPath "$INSTDIR\lib"
	File "..\..\source\demyo-app\target\demyo-app*.jar"
	File "..\..\source\demyo-app\target\dependencies\*.jar"

	SetOutPath "$INSTDIR\legacy-h2-versions"
	File "..\..\source\demyo-app\target\legacy-h2-versions\*.jar"
	
	SetOutPath "$INSTDIR"
	File /oname=Demyo.ico Demyo.ico
	
	# URLs
	WriteINIStr "$INSTDIR\$(SHELL_Official_website).url" "InternetShortcut" "URL" $(SHELL_Website_URL) # Website
	
	# Shortcuts
	DetailPrint "Creating shortcuts"
	!insertmacro MUI_STARTMENU_WRITE_BEGIN CreateStartMenuShortcuts
		SetShellVarContext current
		Push "$SMPROGRAMS\$STARTMENU_FOLDER"
		Call CreateShortcutsIn
	!insertmacro MUI_STARTMENU_WRITE_END
	SetShellVarContext current

	# Create uninstaller
	WriteUninstaller "$INSTDIR\$(SHELL_Uninstall).exe"
	
	# Post-install/update functions
	StrCmp $SETUP_MODE "update" modePostUpdate modePostInstall
	modePostUpdate:
		Call DemyoUpdatePost
		Goto ModePostDone
	modePostInstall:
		Call DemyoInstallPost
	ModePostDone:
SectionEnd

Function DemyoInstallPost
	# Store installation folder
	WriteRegStr ${DEMYO_REG_ROOT} ${DEMYO_REG_KEY} "" $INSTDIR
	WriteRegStr ${DEMYO_REG_ROOT} ${DEMYO_REG_KEY} ${DEMYO_REG_VALUE_VERSION} "2.0"
FunctionEnd

Function DemyoUpdatePre
	# Backup old Demyo
	Rename "$INSTDIR\lib" "$INSTDIR\lib.old"
	Rename "$INSTDIR\war" "$INSTDIR\war.old"
FunctionEnd

Function DemyoUpdatePost
	# Remove backup
	RMDir /r /REBOOTOK "$INSTDIR\lib.old"
	RMDir /r /REBOOTOK "$INSTDIR\war.old"
FunctionEnd

Section /o $(COMP_NAME_DesktopShortcut) COMP_DesktopShortcut
	CreateShortCut "$DESKTOP\$(SHELL_Launch_Demyo).lnk" "$INSTDIR\Demyo.exe" "" "$INSTDIR\Demyo.ico" 0
SectionEnd

Section $(COMP_NAME_JavaAds) COMP_JavaAds
	# Special section to disable Java Ads on upgrade
	# See:
	# https://www.java.com/en/download/faq/disable_offers.xml 
	# http://superuser.com/questions/549028/how-can-i-prevent-ask-com-toolbar-from-being-installed-every-time-java-is-update/562869#562869 

	WriteRegStr HKLM "SOFTWARE\JavaSoft" "SPONSORS" "DISABLE"
	WriteRegStr HKLM "SOFTWARE\Wow6432Node\JavaSoft" "SPONSORS" "DISABLE"
	${If} ${RunningX64}
		WriteRegStr HKLM "SOFTWARE\Wow6432Node\JavaSoft" "SPONSORS" "DISABLE"
	${EndIf}
SectionEnd

Section "Uninstall"
	# Start menu
	!insertmacro MUI_STARTMENU_GETFOLDER CreateStartMenuShortcuts $R0
	SetShellVarContext current
	Delete "$SMPROGRAMS\$R0\$(SHELL_Launch_Demyo).lnk"
	Delete "$SMPROGRAMS\$R0\$(SHELL_Official_website).lnk"
	Delete "$SMPROGRAMS\$R0\$(SHELL_Uninstall).lnk"
	RMDir /REBOOTOK "$SMPROGRAMS\$R0"
	# Desktop
	Delete "$DESKTOP\$(SHELL_Launch_Demyo).lnk"

	# Remove installation directory
	RMDir /r /REBOOTOK $INSTDIR
	
	# Remove registry keys
	DeleteRegKey ${DEMYO_REG_ROOT} ${DEMYO_REG_KEY}
SectionEnd

# Set language. Must be done after pages and uninstall section (else
# MUI_UNTEXT_* aren't registered) but before .onInit function (else
# MUI_LANGDLL_DISPLAY is buggy)
!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "French"

Function .onInit
	# Show language dialog
	!insertmacro MUI_LANGDLL_DISPLAY

	# Demyo is an absolute requirement
	IntOp $0 ${SF_SELECTED} | ${SF_RO}
	IntOp $0 $0 | ${SF_BOLD}
	SectionSetFlags ${COMP_Demyo} $0
FunctionEnd

# This is needed in order to have the right language
# See https://stackoverflow.com/a/14305762/109813
Function onPostInit
	# Check if we update or install
	ClearErrors
	ReadRegStr $0 ${DEMYO_REG_ROOT} ${DEMYO_REG_KEY} ""
	IfErrors notYetInstalled 0
		# If it's an update, check if it's from Demyo 1
		ClearErrors
		ReadRegStr $0 ${DEMYO_REG_ROOT} ${DEMYO_REG_KEY} ${DEMYO_REG_VALUE_VERSION}
		# In case of error reading, it's from Demyo 1
		IfErrors isDemyo1 0
			# If needed, we could do the following
			# StrCmp $R0 '2.0' isDemyo2
			# StrCmp $R0 '3.0' isDemyo3
			Goto isNotDemyo1
		isDemyo1:
			MessageBox MB_OK "$(INSTALL_NoUpgradeFromDemyo1)"
			Abort
		isNotDemyo1:

		StrCpy $SETUP_MODE "update"
		Goto installCheckDone
	notYetInstalled:
		StrCpy $SETUP_MODE "install"
	installCheckDone:
FunctionEnd

Function un.onInit
	# Get language from registry
	!insertmacro MUI_UNGETLANGUAGE
FunctionEnd

Function CreateShortcutsIn
	SetShellVarContext current
	Pop $0 # Output directory
	CreateDirectory "$0"
	CreateShortCut "$0\$(SHELL_Launch_Demyo).lnk" "$INSTDIR\Demyo.exe" "" "$INSTDIR\Demyo.ico" 0
	CreateShortCut "$0\$(SHELL_Official_website).lnk" "$INSTDIR\$(SHELL_Official_website).url"
	CreateShortCut "$0\$(SHELL_Uninstall).lnk" "$INSTDIR\$(SHELL_Uninstall).exe"
FunctionEnd

Function FinishLaunchDemyo
	ShellExecAsUser::ShellExecAsUser "open" "$INSTDIR\Demyo.exe"
FunctionEnd

# Localisation
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
	!insertmacro MUI_DESCRIPTION_TEXT ${COMP_Demyo} $(COMP_DESC_Demyo)
	!insertmacro MUI_DESCRIPTION_TEXT ${COMP_DesktopShortcut} $(COMP_DESC_DesktopShortcut)
	!insertmacro MUI_DESCRIPTION_TEXT ${COMP_JavaAds} $(COMP_DESC_JavaAds)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

# Set translation strings after they have been used
!include "l10n-English.nsh"
!include "l10n-French.nsh"
