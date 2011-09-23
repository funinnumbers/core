(
var r, lala, f, initialPlaytime;

var a, b, c, d;

// this variable holds the number of players currently active on the playing field
var numberOfFinnPlayers;

var tempPlayersNumber;

// keeping an eye on the total playing time of the current track
var timeThatHasPassed, timeThatHasPassedBetweenTracks;

// array for holding the filenames of the samples for the specific track
var filenamesBufferArray;

// array for holding the actual audio samples that will be used inside the synth
var samplesBufferArray;

// array for holding the duration of each sample
var samplesDurationArray;
	
var playingForTheFirstTime;


var playingOuttro;

// keeping an eye on the gaming activity
var somethingHasChanged, somethingHasChangedCounter;

var trackToBePlayed;
var loungeTrackToBePlayed;

/////////////////////// LOUNGE ARRAY



// array for holding the filenames of the samples for the specific track
var loungeFilenamesBufferArray;

// array for holding the actual audio samples that will be used inside the synth
var loungeSamplesBufferArray;

// array for holding the duration of each sample
var loungeSamplesDurationArray;

loungeFilenamesBufferArray = [ //"lounge/1.wav", "lounge/2.wav", "lounge/3.wav", "lounge/4.wav", 
"lounge/5.wav", "lounge/6.wav" , "lounge/7.wav" , "lounge/8.wav", "lounge/9.wav", "lounge/10.wav", "lounge/11.wav" ];

loungeSamplesDurationArray = Array.new(loungeFilenamesBufferArray.size);

// instantiate the samples buffer array and then populate it - samples are loaded directly to memory after being read
loungeSamplesBufferArray = Array.new(loungeFilenamesBufferArray.size);

loungeSamplesBufferArray = loungeFilenamesBufferArray.collect({ arg path;
	Buffer.read(s, "c:/litho/" ++ path);
	});

loungeSamplesDurationArray = loungeFilenamesBufferArray.collect({ arg path;
	f = SoundFile.new;
	f.openRead("c:/litho/" ++ path);
	((f.numFrames)/(f.sampleRate));
	});


///////////////////////// ending lounge array stuff



// x is a global variable - TODO: change to an env variable
x = 0;
trackToBePlayed = 1;

// used to track time between sample repetitions
timeThatHasPassed = 0;
timeThatHasPassedBetweenTracks = 0;
somethingHasChanged = 0;
somethingHasChangedCounter = 0;

// Setting the responders for OSC messages
// The first one when the number of players is increased

a = OSCresponder(nil, '/decreaseNumberOfPlayers', { arg data1, data2;
	"receive: elvis has left".postln;
	//if ( x > 0,
	x = x - 1;//},
	somethingHasChanged = 1;
	somethingHasChangedCounter = 0;//{}
	//);
}).add;


// The second one when the number of players is decreased

b = OSCresponder(nil, '/increaseNumberOfPlayers', { arg data1, data2;
	"receive: elvis has entered".postln;
	//if ( x < (samplesDurationArray.size),
	x = x + 1;//},
	somethingHasChanged = 1;
	somethingHasChangedCounter = 0;//{}
	//);
}).add;

// The third one when the game begins - play lounge music

c = OSCresponder(nil, '/startingUp', { arg data1, data2;
"receive".postln;
//if ( x < (samplesDurationArray.size),
	x = x + 1;//},
	//{}
	//);
}).add;

// The fourth one when the game ends or nothing happens - play lounge music

d = OSCresponder(nil, '/quittingOrPausingGame', { arg data1, data2;
"receive".postln;
//if ( x < (samplesDurationArray.size),
	x = x + 1;//},
	//{}
	//);
}).add;


//tempPlayersNumber = Synth(\selectPlayersNumber);


// Play the samples!
// "Routine" calls wait for the length of the sample being played at the current loop
// 2ms before completing playback the loop is entered again and depending on the 
// variable the respective sample buffer is used to produce audio

r = Routine.new({

	inf.do({ arg i;

	
	var playtime, tempX, playingPassSample;

	playtime = 1;
	playingPassSample = 0;
	playingForTheFirstTime = 1;
	tempX = -1;
	playingOuttro = 0;

	

	if ( somethingHasChangedCounter < 100,

	{
	// populate filenames array
	// last element in the array is a "pass" audio file

	switch(trackToBePlayed,
		//0, {filenamesBufferArray = [ "lounge/1.wav", "lounge/2.wav", "lounge/3.wav", "lounge/4.wav", "lounge/5.wav", "lounge/6.wav" , "lounge/7.wav" , "lounge/8.wav", "lounge/9.wav", "lounge/10.wav", "lounge/11.wav"] },
		1, {filenamesBufferArray = [ "blend/1.wav", "blend/2.wav", "blend/3.wav", "blend/4.wav", "blend/5.wav", "blend/6.wav", "blend/7.wav", "blend/8.wav", "blend/9.wav", "blend/10.wav", "blend/pass.wav"]},
		2, {filenamesBufferArray = [ "herbie/1.wav", "herbie/2.wav", "herbie/3.wav", "herbie/4.wav", "herbie/5.wav", "herbie/6.wav", "herbie/7.wav", "herbie/8.wav", "herbie/9.wav", "herbie/10.wav", "herbie/pass.wav" ] },
		3, {filenamesBufferArray = [ "eomir/1.wav", "eomir/2.wav", "eomir/3.wav", "eomir/4.wav", "eomir/5.wav", "eomir/6.wav", "eomir/7.wav", "eomir/8.wav", "eomir/9.wav", "eomir/10.wav", "eomir/pass.wav" ] }
	);

	samplesDurationArray = Array.new(filenamesBufferArray.size);

	// instantiate the samples buffer array and then populate it - samples are loaded directly to memory after being read
	samplesBufferArray = Array.new(filenamesBufferArray.size);

	samplesBufferArray = filenamesBufferArray.collect({ arg path;
		Buffer.read(s, "c:/litho/" ++ path);
		});

	samplesDurationArray = filenamesBufferArray.collect({ arg path;
		f = SoundFile.new;
		f.openRead("c:/litho/" ++ path);
		((f.numFrames)/(f.sampleRate));
		});


	samplesDurationArray.postln;

	while( { timeThatHasPassedBetweenTracks < 120 }, {

// checking the status of the current number of players
// if it larger than the value of the counter when the previous sample was scheduled we increment the internal counter by one
// in order to have a smooth sample playing transition (and e.g.,  not go from 1 to 8 in 4 secs)

		if (x > (samplesBufferArray.size),
			{x = (samplesBufferArray.size) - 2},
			{}
		);

		if (x < 0,
			{x = 0},
			{}
		);

		if ( tempX < x ,
			{tempX = tempX + 1;},
			{tempX = x;}
		);

		"tempX = ".post;
		tempX.postln;

// here we schedule the next sample to be played 
// if there has passed a minute we schedule a "pass" sample to be more colourful :)

		if ( ( timeThatHasPassed - 40 ) > 0,
			{if ( timeThatHasPassed + samplesDurationArray[(samplesBufferArray.size) - 1] > 130,
				{lala = Synth(\samplerTOWOuttro, [\bufnum, (samplesBufferArray[(samplesBufferArray.size) - 1].bufnum),  \duration,  samplesDurationArray[(samplesBufferArray.size) - 1]]);},
				{lala = Synth(\samplerTOW, [\bufnum, (samplesBufferArray[(samplesBufferArray.size) - 1].bufnum) ]);}
			);
			 playingPassSample = 1;},
			{ if ( tempX <= ((samplesBufferArray.size) - 1) , 
				{switch (playingForTheFirstTime,
					0, { if ( timeThatHasPassed + samplesDurationArray[tempX] > 130,
						{lala = Synth(\samplerTOWOuttro, [\bufnum, (samplesBufferArray[tempX].bufnum),  \duration,  samplesDurationArray[tempX]]);},
						{lala = Synth(\samplerTOW, [\bufnum, (samplesBufferArray[tempX].bufnum) ]);}
					);
					},
//lala = Synth(\samplerTOW, [\bufnum, (samplesBufferArray[tempX].bufnum) ]);},
					1, {lala = Synth(\samplerTOWintro, [\bufnum, (samplesBufferArray[0].bufnum), \duration,  samplesDurationArray[0]]);
					playingForTheFirstTime = 0;}
				);}, 
				{tempX = (samplesBufferArray.size) - 2;}
			);}
		);

		
		
// here we calculate the total duration of the sample - #frames in the buffer / #frames per second
// then we call wait to pause for the duration of the sample minus 1ms

		//( ( (samplesBufferArray[i].numFrames) / (samplesBufferArray[i].sampleRate) ) - 0.002 ).wait;   //do not miss me out!
		if ( playingPassSample == 1,
			{playingPassSample = 0;
			 timeThatHasPassed = 0;
			 playtime = samplesDurationArray[(samplesBufferArray.size) - 1];},
			{if ( tempX < (samplesBufferArray.size),
				{playtime = samplesDurationArray[tempX]; },
				{playtime = samplesDurationArray[(samplesBufferArray.size) - 2];}
			);}
		);
		
		
		
		timeThatHasPassed = timeThatHasPassed + samplesDurationArray[tempX];
		timeThatHasPassedBetweenTracks = timeThatHasPassedBetweenTracks + samplesDurationArray[tempX];
		timeThatHasPassed.postln;
		timeThatHasPassedBetweenTracks.postln;
		//x = x + 1;
		//samplesDurationArray[tempX].numFrames.postln;

		somethingHasChangedCounter = somethingHasChangedCounter + playtime;

		playtime.wait;
	
	}); // while closes here

	// reset the counter and then change the track pointer
	timeThatHasPassedBetweenTracks = 0;
	timeThatHasPassed = 0;

	if (trackToBePlayed < 3 ,
		{trackToBePlayed = trackToBePlayed + 1;},
		{trackToBePlayed = 1}
	);


	}, // first case if activity is monitored closes heres
	{
		// if no activity is monitored, play one lounge track
	somethingHasChanged = 0;
	loungeTrackToBePlayed = (6.rand);
	lala = Synth(\samplerTOW, [\bufnum, (loungeSamplesBufferArray[loungeTrackToBePlayed].bufnum) ]);
	loungeSamplesDurationArray[loungeTrackToBePlayed].wait;
	}
	);


}); // inf.do closes here


});

r.play;

)
                                                                                                                                                                     