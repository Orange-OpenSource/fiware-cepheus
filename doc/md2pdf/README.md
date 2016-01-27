#How to generate a PDF document from a set of Markdown files with Docker ?

Follows the guide: [Fiware markdown to PDF](https://github.com/FiwareULPGC/fiware-markdown-to-pdf#quick-start-guide-with-docker)

## User Guide

    git clone https://github.com/Orange-OpenSource/fiware-cepheus.git
    cd fiware-cepheus
    docker run -v=/Users/{UserName}/fiware-cepheus:/md2pdf fiwareulpgc/markdown-to-pdf -i /md2pdf/doc/md2pdf/md2pdf.yml -o /md2pdf/user-guide.pdf

## Admin Guide

    git clone https://github.com/Orange-OpenSource/fiware-cepheus.git
    cd fiware-cepheus
    docker run -v=/Users/{UserName}/fiware-cepheus:/md2pdf fiwareulpgc/markdown-to-pdf -i /md2pdf/doc/md2pdf/md2pdf_admin.yml -o /md2pdf/admin-guide.pdf

