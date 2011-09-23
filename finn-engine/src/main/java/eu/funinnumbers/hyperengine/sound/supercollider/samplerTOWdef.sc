// to be copied in the startup.sc file in the supercollider folder
// includes definitions for basic synths that play the .wav files stored in the buffers

SynthDef(\samplerTOW, { |bufnum = 0, outbus = 0, amp = 0.4, loop = 0, rate = 1.0|
	var data;
	data = PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, 0, 0, loop);
	FreeSelfWhenDone.kr(data);
	Out.ar(outbus, data * amp);
}).store;



SynthDef(\samplerTOWouttro, { |bufnum = 0, outbus = 0, amp = 0.4, loop = 1, rate = 1.0, duration = 4|
	var data, env_gen, env;
	env = Env.new([1, 0], [duration], 'linear');
	env_gen = EnvGen.kr(env);
	data = PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, 0, 0, loop);
	FreeSelfWhenDone.kr(data);
	Out.ar(outbus, data * (amp * env_gen));
}).store;



SynthDef(\samplerTOWintro, { |bufnum = 0, outbus = 0, amp = 0.4, loop = 1, rate = 1.0, duration = 2|
	var data, env_gen, env;
	env = Env.new([0, 1, 0], [duration, duration + 0.002], 'linear');
	env_gen = EnvGen.kr(env);
	data = PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, 0, 0, loop);
	FreeSelfWhenDone.kr(data);
	Out.ar(outbus, data * (amp * env_gen) );
}).store;
                   
SynthDef(\samplerLounge, { |bufnum = 0, outbus = 0, amp = 0.4, loop = 0, rate = 1.0|
	var data;
	data = PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum) * rate, 0, 0, loop);
	FreeSelfWhenDone.kr(data);
	Out.ar(outbus, data * amp);
}).store; 