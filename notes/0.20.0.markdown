Updated regions and AMIs (see #56):

- Updated AWS Java SDK from 1.11.205 to 1.11.218
- Added `CanadaCentral` and `London` region aliases
- Updated all AMIs to the 2017.09 version IDs (+ new ones for the new regions)
- Added tests to facilitate future maintenance:
    + to check aliases <-> SDK regions enum correspondence
    + to check that all defined AMIs actually exist and have correct properties
