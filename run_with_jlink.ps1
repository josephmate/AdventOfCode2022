if (Test-Path -Path "customjar") {
    "customjar exists!"
} else {
  jlink --output customjar --add-modules java.base
}
.\customjar\bin\java.exe -cp out/production/AdventOfCode2022 aoc.Main 4
