#include "dib.h"


// For mingw
#ifndef _MSC_VER
#define fopen_s(a,b,c) *(a)=fopen(b,c)
#endif



void DIB::saveBMP(char *fileName, bool flip)
{
	FILE *outFile;
		
	int bfSize = 54+1024+width*height;
	int offs,dOffs;
                                 		
	fopen_s(&outFile, fileName, "wb");
	char bmpHeader[] = {'B','M',
		                bfSize&0xff,bfSize>>8,bfSize>>16,bfSize>>24,
		                0,0,0,0,
		                (54+1024)&0xff,(54+1024)>>8,0,0,
		                40,0,0,0,
		                width&0xff,width>>8,0,0,
		                height&0xff,height>>8,0,0,
		                1,0,
		                8,0,
		                0,0,0,0,
		                0,0,0,0,
		                0,0,0,0,
		                0,0,0,0,
		                0,0,0,0,
		                0,0,0,0};
	for (int i=0; i<54; i++)
		fputc(bmpHeader[i], outFile);
	for (int i=0; i<256; i++)
	{
		fputc(palette[i*3 + 2], outFile);
		fputc(palette[i*3 + 1], outFile);
		fputc(palette[i*3 + 0], outFile);
		fputc(0, outFile);
	}
	if (flip)
	{
		offs = (height-1)*width;
		dOffs = -width;
	}
	else
	{
		offs = 0;
		dOffs = width;
	}
	for (int y=0; y<height; y++)
	{
		for (int x=0; x<width; x++)
			fputc(bits[offs+x], outFile);
		offs += dOffs;
	}
	fclose(outFile);
}
