# http://nsis.sourceforge.net/Convert_Unix_new-lines_(%5Cn)_to_Windows_(%5Cr%5Cn)
# This script was made for Dreas to convert a text file with Unix new-lines (\n) to Windows (\r\n) after a file download. When left in Unix format, programs such as Windows Notepad do not display the text file properly. 
#
# Usage:
# Push "path\to\text_file.txt"
# Call ConvertUnixNewLines
Function ConvertUnixNewLines
	Exch $R0 ;file #1 path
	Push $R1 ;file #1 handle
	Push $R2 ;file #2 path
	Push $R3 ;file #2 handle
	Push $R4 ;data
	Push $R5

	FileOpen $R1 $R0 r
	GetTempFileName $R2
	FileOpen $R3 $R2 w
 
	loopRead:
		ClearErrors
		FileRead $R1 $R4 
		IfErrors doneRead
 
		StrCpy $R5 $R4 1 -1
		StrCmp $R5 $\n 0 +4
		StrCpy $R5 $R4 1 -2
		StrCmp $R5 $\r +3
		StrCpy $R4 $R4 -1
		StrCpy $R4 "$R4$\r$\n"
 
		FileWrite $R3 $R4
 
		Goto loopRead
	doneRead:

	FileClose $R3
	FileClose $R1

	SetDetailsPrint none
	Delete $R0
	Rename $R2 $R0
	SetDetailsPrint both

	Pop $R5
	Pop $R4
	Pop $R3
	Pop $R2
	Pop $R1
	Pop $R0
FunctionEnd