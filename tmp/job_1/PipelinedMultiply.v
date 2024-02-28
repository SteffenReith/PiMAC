// Generator : SpinalHDL v1.10.1    git head : 2527c7c6b0fb0f95e5e1a5722a0be732b364ce43
// Component : PipelinedMultiply
// Git hash  : 2b439375f770d03da74c0e75a86664f48e9dee1b

`timescale 1ns/1ps

module PipelinedMultiply (
  input  wire          _zz_when_StageLink_l67_31,
  output wire          _zz_49,
  output wire          _zz_50,
  input  wire          _zz_51,
  output wire [31:0]   _zz_52,
  input  wire          clk,
  input  wire          reset
);

  wire                _zz_1;
  wire                _zz_when_StageLink_l67;
  wire                _zz_2;
  wire                _zz_when_StageLink_l67_1;
  wire                _zz_3;
  wire                _zz_when_StageLink_l67_2;
  wire                _zz_4;
  wire                _zz_when_StageLink_l67_3;
  wire                _zz_5;
  wire                _zz_when_StageLink_l67_4;
  wire                _zz_6;
  wire                _zz_when_StageLink_l67_5;
  wire                _zz_7;
  wire                _zz_when_StageLink_l67_6;
  wire                _zz_8;
  wire                _zz_when_StageLink_l67_7;
  wire                _zz_9;
  wire                _zz_when_StageLink_l67_8;
  wire                _zz_10;
  wire                _zz_when_StageLink_l67_9;
  wire                _zz_11;
  wire                _zz_when_StageLink_l67_10;
  wire                _zz_12;
  wire                _zz_when_StageLink_l67_11;
  wire                _zz_13;
  wire                _zz_when_StageLink_l67_12;
  wire                _zz_14;
  wire                _zz_when_StageLink_l67_13;
  wire                _zz_15;
  wire                _zz_when_StageLink_l67_14;
  wire                _zz_16;
  reg        [31:0]   _zz_17;
  reg                 _zz_when_StageLink_l67_15;
  reg                 _zz_when_StageLink_l67_16;
  reg                 _zz_when_StageLink_l67_17;
  reg                 _zz_when_StageLink_l67_18;
  reg                 _zz_when_StageLink_l67_19;
  reg                 _zz_when_StageLink_l67_20;
  reg                 _zz_when_StageLink_l67_21;
  reg                 _zz_when_StageLink_l67_22;
  reg                 _zz_when_StageLink_l67_23;
  reg                 _zz_when_StageLink_l67_24;
  reg                 _zz_when_StageLink_l67_25;
  reg                 _zz_when_StageLink_l67_26;
  reg                 _zz_when_StageLink_l67_27;
  reg                 _zz_when_StageLink_l67_28;
  reg                 _zz_when_StageLink_l67_29;
  reg                 _zz_18;
  reg                 _zz_19;
  reg                 _zz_20;
  reg                 _zz_21;
  reg                 _zz_22;
  reg                 _zz_23;
  reg                 _zz_24;
  reg                 _zz_25;
  reg                 _zz_26;
  reg                 _zz_27;
  reg                 _zz_28;
  reg                 _zz_29;
  reg                 _zz_30;
  reg                 _zz_31;
  reg                 _zz_32;
  reg                 _zz_when_StageLink_l67_30;
  reg        [31:0]   _zz_33;
  reg        [31:0]   _zz_34;
  reg        [31:0]   _zz_35;
  reg        [31:0]   _zz_36;
  reg        [31:0]   _zz_37;
  reg        [31:0]   _zz_38;
  reg        [31:0]   _zz_39;
  reg        [31:0]   _zz_40;
  reg        [31:0]   _zz_41;
  reg        [31:0]   _zz_42;
  reg        [31:0]   _zz_43;
  reg        [31:0]   _zz_44;
  reg        [31:0]   _zz_45;
  reg        [31:0]   _zz_46;
  reg        [31:0]   _zz_47;
  reg                 _zz_48;
  wire                when_StageLink_l67;
  wire                when_StageLink_l67_1;
  wire                when_StageLink_l67_2;
  wire                when_StageLink_l67_3;
  wire                when_StageLink_l67_4;
  wire                when_StageLink_l67_5;
  wire                when_StageLink_l67_6;
  wire                when_StageLink_l67_7;
  wire                when_StageLink_l67_8;
  wire                when_StageLink_l67_9;
  wire                when_StageLink_l67_10;
  wire                when_StageLink_l67_11;
  wire                when_StageLink_l67_12;
  wire                when_StageLink_l67_13;
  wire                when_StageLink_l67_14;
  wire                when_StageLink_l67_15;

  assign _zz_49 = _zz_48;
  assign _zz_50 = _zz_when_StageLink_l67_30;
  assign _zz_52 = _zz_33;
  always @(*) begin
    _zz_48 = _zz_18;
    if(when_StageLink_l67) begin
      _zz_48 = 1'b1;
    end
  end

  assign when_StageLink_l67 = (! _zz_when_StageLink_l67_14);
  always @(*) begin
    _zz_18 = _zz_19;
    if(when_StageLink_l67_1) begin
      _zz_18 = 1'b1;
    end
  end

  assign when_StageLink_l67_1 = (! _zz_when_StageLink_l67_13);
  always @(*) begin
    _zz_19 = _zz_20;
    if(when_StageLink_l67_2) begin
      _zz_19 = 1'b1;
    end
  end

  assign when_StageLink_l67_2 = (! _zz_when_StageLink_l67_12);
  always @(*) begin
    _zz_20 = _zz_21;
    if(when_StageLink_l67_3) begin
      _zz_20 = 1'b1;
    end
  end

  assign when_StageLink_l67_3 = (! _zz_when_StageLink_l67_11);
  always @(*) begin
    _zz_21 = _zz_22;
    if(when_StageLink_l67_4) begin
      _zz_21 = 1'b1;
    end
  end

  assign when_StageLink_l67_4 = (! _zz_when_StageLink_l67_10);
  always @(*) begin
    _zz_22 = _zz_23;
    if(when_StageLink_l67_5) begin
      _zz_22 = 1'b1;
    end
  end

  assign when_StageLink_l67_5 = (! _zz_when_StageLink_l67_9);
  always @(*) begin
    _zz_23 = _zz_24;
    if(when_StageLink_l67_6) begin
      _zz_23 = 1'b1;
    end
  end

  assign when_StageLink_l67_6 = (! _zz_when_StageLink_l67_8);
  always @(*) begin
    _zz_24 = _zz_25;
    if(when_StageLink_l67_7) begin
      _zz_24 = 1'b1;
    end
  end

  assign when_StageLink_l67_7 = (! _zz_when_StageLink_l67_7);
  always @(*) begin
    _zz_25 = _zz_26;
    if(when_StageLink_l67_8) begin
      _zz_25 = 1'b1;
    end
  end

  assign when_StageLink_l67_8 = (! _zz_when_StageLink_l67_6);
  always @(*) begin
    _zz_26 = _zz_27;
    if(when_StageLink_l67_9) begin
      _zz_26 = 1'b1;
    end
  end

  assign when_StageLink_l67_9 = (! _zz_when_StageLink_l67_5);
  always @(*) begin
    _zz_27 = _zz_28;
    if(when_StageLink_l67_10) begin
      _zz_27 = 1'b1;
    end
  end

  assign when_StageLink_l67_10 = (! _zz_when_StageLink_l67_4);
  always @(*) begin
    _zz_28 = _zz_29;
    if(when_StageLink_l67_11) begin
      _zz_28 = 1'b1;
    end
  end

  assign when_StageLink_l67_11 = (! _zz_when_StageLink_l67_3);
  always @(*) begin
    _zz_29 = _zz_30;
    if(when_StageLink_l67_12) begin
      _zz_29 = 1'b1;
    end
  end

  assign when_StageLink_l67_12 = (! _zz_when_StageLink_l67_2);
  always @(*) begin
    _zz_30 = _zz_31;
    if(when_StageLink_l67_13) begin
      _zz_30 = 1'b1;
    end
  end

  assign when_StageLink_l67_13 = (! _zz_when_StageLink_l67_1);
  always @(*) begin
    _zz_31 = _zz_32;
    if(when_StageLink_l67_14) begin
      _zz_31 = 1'b1;
    end
  end

  assign when_StageLink_l67_14 = (! _zz_when_StageLink_l67);
  always @(*) begin
    _zz_32 = _zz_51;
    if(when_StageLink_l67_15) begin
      _zz_32 = 1'b1;
    end
  end

  assign when_StageLink_l67_15 = (! _zz_when_StageLink_l67_30);
  assign _zz_16 = _zz_48;
  assign _zz_when_StageLink_l67_14 = _zz_when_StageLink_l67_29;
  assign _zz_15 = _zz_18;
  assign _zz_when_StageLink_l67_13 = _zz_when_StageLink_l67_28;
  assign _zz_14 = _zz_19;
  assign _zz_when_StageLink_l67_12 = _zz_when_StageLink_l67_27;
  assign _zz_13 = _zz_20;
  assign _zz_when_StageLink_l67_11 = _zz_when_StageLink_l67_26;
  assign _zz_12 = _zz_21;
  assign _zz_when_StageLink_l67_10 = _zz_when_StageLink_l67_25;
  assign _zz_11 = _zz_22;
  assign _zz_when_StageLink_l67_9 = _zz_when_StageLink_l67_24;
  assign _zz_10 = _zz_23;
  assign _zz_when_StageLink_l67_8 = _zz_when_StageLink_l67_23;
  assign _zz_9 = _zz_24;
  assign _zz_when_StageLink_l67_7 = _zz_when_StageLink_l67_22;
  assign _zz_8 = _zz_25;
  assign _zz_when_StageLink_l67_6 = _zz_when_StageLink_l67_21;
  assign _zz_7 = _zz_26;
  assign _zz_when_StageLink_l67_5 = _zz_when_StageLink_l67_20;
  assign _zz_6 = _zz_27;
  assign _zz_when_StageLink_l67_4 = _zz_when_StageLink_l67_19;
  assign _zz_5 = _zz_28;
  assign _zz_when_StageLink_l67_3 = _zz_when_StageLink_l67_18;
  assign _zz_4 = _zz_29;
  assign _zz_when_StageLink_l67_2 = _zz_when_StageLink_l67_17;
  assign _zz_3 = _zz_30;
  assign _zz_when_StageLink_l67_1 = _zz_when_StageLink_l67_16;
  assign _zz_2 = _zz_31;
  assign _zz_when_StageLink_l67 = _zz_when_StageLink_l67_15;
  assign _zz_1 = _zz_32;
  always @(posedge clk) begin
    _zz_47 <= (_zz_46 + 32'h00000001);
    _zz_45 <= (_zz_47 + 32'h00000001);
    _zz_44 <= (_zz_45 + 32'h00000001);
    _zz_43 <= (_zz_44 + 32'h00000001);
    _zz_42 <= (_zz_43 + 32'h00000001);
    _zz_41 <= (_zz_42 + 32'h00000001);
    _zz_40 <= (_zz_41 + 32'h00000001);
    _zz_39 <= (_zz_40 + 32'h00000001);
    _zz_38 <= (_zz_39 + 32'h00000001);
    _zz_37 <= (_zz_38 + 32'h00000001);
    _zz_36 <= (_zz_37 + 32'h00000001);
    _zz_35 <= (_zz_36 + 32'h00000001);
    _zz_34 <= (_zz_35 + 32'h00000001);
    _zz_33 <= (_zz_34 + 32'h00000001);
    if(_zz_16) begin
      _zz_17 <= 32'h00000000;
    end
    if(_zz_15) begin
      _zz_46 <= _zz_17;
    end
    if(_zz_14) begin
      _zz_47 <= _zz_46;
    end
    if(_zz_13) begin
      _zz_45 <= _zz_47;
    end
    if(_zz_12) begin
      _zz_44 <= _zz_45;
    end
    if(_zz_11) begin
      _zz_43 <= _zz_44;
    end
    if(_zz_10) begin
      _zz_42 <= _zz_43;
    end
    if(_zz_9) begin
      _zz_41 <= _zz_42;
    end
    if(_zz_8) begin
      _zz_40 <= _zz_41;
    end
    if(_zz_7) begin
      _zz_39 <= _zz_40;
    end
    if(_zz_6) begin
      _zz_38 <= _zz_39;
    end
    if(_zz_5) begin
      _zz_37 <= _zz_38;
    end
    if(_zz_4) begin
      _zz_36 <= _zz_37;
    end
    if(_zz_3) begin
      _zz_35 <= _zz_36;
    end
    if(_zz_2) begin
      _zz_34 <= _zz_35;
    end
    if(_zz_1) begin
      _zz_33 <= _zz_34;
    end
  end

  always @(posedge clk or posedge reset) begin
    if(reset) begin
      _zz_when_StageLink_l67_29 <= 1'b0;
      _zz_when_StageLink_l67_28 <= 1'b0;
      _zz_when_StageLink_l67_27 <= 1'b0;
      _zz_when_StageLink_l67_26 <= 1'b0;
      _zz_when_StageLink_l67_25 <= 1'b0;
      _zz_when_StageLink_l67_24 <= 1'b0;
      _zz_when_StageLink_l67_23 <= 1'b0;
      _zz_when_StageLink_l67_22 <= 1'b0;
      _zz_when_StageLink_l67_21 <= 1'b0;
      _zz_when_StageLink_l67_20 <= 1'b0;
      _zz_when_StageLink_l67_19 <= 1'b0;
      _zz_when_StageLink_l67_18 <= 1'b0;
      _zz_when_StageLink_l67_17 <= 1'b0;
      _zz_when_StageLink_l67_16 <= 1'b0;
      _zz_when_StageLink_l67_15 <= 1'b0;
      _zz_when_StageLink_l67_30 <= 1'b0;
    end else begin
      if(_zz_16) begin
        _zz_when_StageLink_l67_29 <= _zz_when_StageLink_l67_31;
      end
      if(_zz_15) begin
        _zz_when_StageLink_l67_28 <= _zz_when_StageLink_l67_14;
      end
      if(_zz_14) begin
        _zz_when_StageLink_l67_27 <= _zz_when_StageLink_l67_13;
      end
      if(_zz_13) begin
        _zz_when_StageLink_l67_26 <= _zz_when_StageLink_l67_12;
      end
      if(_zz_12) begin
        _zz_when_StageLink_l67_25 <= _zz_when_StageLink_l67_11;
      end
      if(_zz_11) begin
        _zz_when_StageLink_l67_24 <= _zz_when_StageLink_l67_10;
      end
      if(_zz_10) begin
        _zz_when_StageLink_l67_23 <= _zz_when_StageLink_l67_9;
      end
      if(_zz_9) begin
        _zz_when_StageLink_l67_22 <= _zz_when_StageLink_l67_8;
      end
      if(_zz_8) begin
        _zz_when_StageLink_l67_21 <= _zz_when_StageLink_l67_7;
      end
      if(_zz_7) begin
        _zz_when_StageLink_l67_20 <= _zz_when_StageLink_l67_6;
      end
      if(_zz_6) begin
        _zz_when_StageLink_l67_19 <= _zz_when_StageLink_l67_5;
      end
      if(_zz_5) begin
        _zz_when_StageLink_l67_18 <= _zz_when_StageLink_l67_4;
      end
      if(_zz_4) begin
        _zz_when_StageLink_l67_17 <= _zz_when_StageLink_l67_3;
      end
      if(_zz_3) begin
        _zz_when_StageLink_l67_16 <= _zz_when_StageLink_l67_2;
      end
      if(_zz_2) begin
        _zz_when_StageLink_l67_15 <= _zz_when_StageLink_l67_1;
      end
      if(_zz_1) begin
        _zz_when_StageLink_l67_30 <= _zz_when_StageLink_l67;
      end
    end
  end


endmodule
