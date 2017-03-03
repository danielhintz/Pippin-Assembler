.data
x: 100
: 200
: 300
array: 30
: 54
: 69
: 13

.text
main:
	//Read array starting from label
	lod #array
	sto 104
	jump #readarray

back:
	sto 102
	cmpz 102
	not
	jmpz #finish
	lod 100
	add #1
	sto 100
	jump #readarray

readarray:
	lod 104
	add 100
	sto 101
	lod &101
	sto 103
	
	jump #back

finish:
	halt