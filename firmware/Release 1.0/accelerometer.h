#ifndef __ACCELEROMETER_H__
#define __ACCELEROMETER_H__

//#include "defs.h"


// STATUS MASKS
//
//  These can be used to determine the meaning of the status byte.
//  FRONT/BACK/TAP/ERR/SHAKE can be used as: if(acc.status & ACC_FRONT)
//  However, LEFT/RIGHT/DOWN/UP are multibit masks, so you need:
//  if((acc.status & ACC_UP) == ACC_UP) to fully differentiate
//
#define ACC_FRONT		0x01
#define ACC_BACK		0x02
#define ACC_LEFT		0x04
#define ACC_RIGHT		0x08
#define ACC_DOWN		0x14
#define ACC_UP			0x18
#define ACC_O_MASK		0x1F
#define ACC_TAP			0x20
#define	ACC_ERR			0x40
#define ACC_SHAKE		0x80

// Create a struct to hold the data from the accelerometer
typedef struct accel_data_struct
{
	uint8_t x,y,z;
	uint8_t status;

} AccelData;

AccelData 	getAccelData(void);	// returns a copy of the raw accel data for user inspection

//------------ User Macros ---------------------------
//  Returns 0 when false, non-zero when true
//
//	Note: Front/Back/Left/Right are based on accelerometer orientation, not block orientation.
//	Futhermore, some can only be determined after several orientation transitions, so this data may not be useful at all.
#define CheckTap(x)				(x->status & ACC_TAP)
#define CheckShake(x)			(x->status & ACC_SHAKE)
#define GetOrientation(x)		(x->status & ACC_O_MASK)
#define isOrientationFront(x)	((x->status & ACC_O_MASK) == ACC_FRONT)
#define isOrientationBack(x)	((x->status & ACC_O_MASK) == ACC_BACK)
#define isOrientationLeft(x)	((x->status & ACC_O_MASK) == ACC_LEFT)
#define isOrientationRight(x)	((x->status & ACC_O_MASK) == ACC_RIGHT)
#define isOrientationDown(x)	((x->status & ACC_O_MASK) == ACC_DOWN)
#define isOrientationUp(x)		((x->status & ACC_O_MASK) == ACC_UP)

//------------ Handler Side Effects
//	The following handlers may be called by the accelerometer:
//	
//	EVENT_ACCEL_CHANGE :	Called on receipt of new accelerometer data; supercedes all handlers below
//	EVENT_ACCEL_TAP:		Called when a tap or orientation change is detected
//	EVENT_ACCEL_SHAKE:		Called when a shake is detected
//


void 		initAccel(void);				// initialization
void 		updateAccel(void);			// Updates the raw accel data from the I2C buffer
int 		newAccelData(void);			// returns 1 if new data in I2C buffer, 0 if not or error

#endif