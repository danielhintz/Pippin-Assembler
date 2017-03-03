.data
factorial: 8

.text
main:
	//Calculate factorial starting from 1
	lod #1

	//Store it at 2 and 3 for future use
	sto 2
	sto 3
	jump #loop

loop:
	lod 3

	//Multiply by what's in memory
	mul 2

	//store back in memory
	sto 2
	
	lod 3
	add #1
	sto 3

	//Load the counter
	lod factorial
	sub #1
	sto factorial
	not
	jmpz #loop
	jump #done

//End the program
done: 
	lod 2
	halt